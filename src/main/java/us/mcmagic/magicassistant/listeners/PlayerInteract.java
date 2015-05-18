package us.mcmagic.magicassistant.listeners;

import org.bukkit.*;
import org.bukkit.block.Sign;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.ItemMeta;
import us.mcmagic.magicassistant.MagicAssistant;
import us.mcmagic.magicassistant.handlers.HotelRoom;
import us.mcmagic.magicassistant.handlers.PlayerData;
import us.mcmagic.magicassistant.handlers.Warp;
import us.mcmagic.magicassistant.utils.HotelUtil;
import us.mcmagic.magicassistant.utils.InventoryType;
import us.mcmagic.magicassistant.utils.WarpUtil;
import us.mcmagic.mcmagiccore.MCMagicCore;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class PlayerInteract implements Listener {
    public static String disposal = ChatColor.BLUE + "[Disposal]";
    public static String warp = ChatColor.BLUE + "[Warp]";
    public static String hotel = ChatColor.BLUE + "[Hotel]";
    static MagicAssistant pl;

    public PlayerInteract(MagicAssistant instance) {
        pl = instance;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        PlayerData data = MagicAssistant.getPlayerData(player.getUniqueId());
        Action action = event.getAction();
        if (action.equals(Action.PHYSICAL)) {
            return;
        }
        if (action.name().toLowerCase().contains("block")) {
            if (player.getItemInHand().getType().equals(Material.DIAMOND_AXE)) {
                if (action.equals(Action.LEFT_CLICK_BLOCK)) {
                    event.setCancelled(true);
                    MagicAssistant.blockChanger.setSelection(0, player, event.getClickedBlock().getLocation());
                } else {
                    event.setCancelled(true);
                    MagicAssistant.blockChanger.setSelection(1, player, event.getClickedBlock().getLocation());
                }
                return;
            }
        }
        if (action.equals(Action.RIGHT_CLICK_BLOCK)) {
            Material type = event.getClickedBlock().getType();
            if (type.equals(Material.SIGN) || type.equals(Material.SIGN_POST) || type.equals(Material.WALL_SIGN)) {
                Sign s = (Sign) event.getClickedBlock().getState();
                if (s.getLine(0).equals(disposal)) {
                    player.openInventory(Bukkit.createInventory(player, 54, ChatColor.BLUE + "Disposal"));
                    return;
                }
                if (s.getLine(0).equals(warp)) {
                    Warp warp = WarpUtil.findWarp(ChatColor.stripColor(s.getLine(1)));
                    if (warp == null) {
                        player.sendMessage(ChatColor.RED + "That warp does not exist, sorry!");
                        return;
                    }
                    if (!warp.getServer().equalsIgnoreCase(MCMagicCore.getMCMagicConfig().serverName)) {
                        WarpUtil.crossServerWarp(player.getUniqueId().toString(), warp.getName(), warp.getServer());
                        return;
                    }
                    player.teleport(warp.getLocation());
                    player.sendMessage(ChatColor.BLUE + "You have arrived at "
                            + ChatColor.WHITE + "[" + ChatColor.GREEN + warp.getName()
                            + ChatColor.WHITE + "]");
                    return;
                }
                if (s.getLine(0).equals(hotel)) {
                    String roomName = ChatColor.stripColor(s.getLine(2)) + " #"
                            + ChatColor.stripColor(s.getLine(1));
                    HotelRoom room = HotelUtil.getRoom(roomName);
                    if (room == null) {
                        player.sendMessage(ChatColor.RED + "That room is out of service right now, sorry!");
                        return;
                    }
                    if (room.isOccupied() && room.getCurrentOccupant().equals(player.getUniqueId())) {
                        MagicAssistant.inventoryUtil.openSpecificHotelRoomCheckoutPage(player, room);
                    } else if (room.isOccupied() && !(room.getCurrentOccupant().equals(player.getUniqueId()))) {
                        player.sendMessage(ChatColor.RED + "This room is already occupied!");
                    } else {
                        boolean playerOwnsRooms = false;
                        for (HotelRoom r : HotelUtil.getRooms()) {
                            if (r.isOccupied() && r.getCurrentOccupant().equals(player.getUniqueId())) {
                                playerOwnsRooms = true;
                                break;
                            }
                        }
                        if (playerOwnsRooms) {
                            player.sendMessage(ChatColor.RED + "You cannot book more than one room at a time!  You need to wait for your current reservation to lapse or check out by right-clicking the booked room's sign.");
                            return;
                        }

                        MagicAssistant.inventoryUtil.openSpecificHotelRoomPage(player, HotelUtil.getRoom(roomName));
                    }
                    return;
                }
            } else if (type.name().toLowerCase().contains("_door")) {
                HotelRoom room = HotelUtil.getRoomFromDoor(event.getClickedBlock(), player);
                if (room != null) {
                    if (room.isOccupied()) {
                        List<UUID> friends = data.getFriendList();
                        if (friends.contains(room.getCurrentOccupant())) {
                            PlayerData target = MagicAssistant.getPlayerData(room.getCurrentOccupant());
                            if (target == null) {
                                player.sendMessage(ChatColor.RED + "Your friend must be online for you to access their room!");
                                event.setCancelled(true);
                                return;
                            }
                            if (!target.getHotel()) {
                                player.sendMessage(ChatColor.RED + "That room is currently occupied.");
                                event.setCancelled(true);
                            }
                            return;
                        }
                        if (room.getCurrentOccupant().equals(player.getUniqueId())) {
                            if (room.getCheckoutTime() <= (System.currentTimeMillis() / 1000)) {
                                event.setCancelled(true);
                                HotelUtil.checkout(room, true);
                                return;
                            }
                            return;
                        } else {
                            player.sendMessage(ChatColor.RED + "That room is currently occupied.");
                            event.setCancelled(true);
                        }
                    } else {
                        player.sendMessage(ChatColor.GREEN + "That room is currently unoccupied. Book your stay by right-clicking the sign or viewing the room in your MagicBand.");
                        event.setCancelled(true);
                    }
                }
                return;
            } else if (type == Material.SKULL) {
                Skull skull = (Skull) event.getClickedBlock().getState();
                if (skull.getSkullType() == SkullType.PLAYER) {
                    if (skull.getOwner() != null) {
                        if (skull.getOwner().equalsIgnoreCase("Telephone") || skull.getOwner().equalsIgnoreCase("a15f8f85-3f97-4c4e-a61d-43b5ad6aafec")) {
                            player.sendMessage(ChatColor.RED + "You cannot use hotel room telephones yet!");
                            return;
                        }
                    }
                }
                return;
            }
        }
        PlayerInventory pi = player.getInventory();
        if (pi.getHeldItemSlot() != 8) {
            return;
        }
        ItemStack mb;
        if (data.getSpecial()) {
            mb = new ItemStack(MagicAssistant.bandUtil.getBandMaterial(data.getBandColor()));
            ItemMeta mbm = mb.getItemMeta();
            mbm.setDisplayName(data.getBandName() + "MagicBand");
            mbm.setLore(Arrays.asList(ChatColor.GREEN + "Click me to open",
                    ChatColor.GREEN + "the MagicBand menu!"));
            mb.setItemMeta(mbm);
        } else {
            mb = new ItemStack(Material.FIREWORK_CHARGE);
            FireworkEffectMeta mbm = (FireworkEffectMeta) mb.getItemMeta();
            mbm.setEffect(FireworkEffect.builder().withColor(MagicAssistant.bandUtil.getBandColor(
                    data.getBandColor())).build());
            mbm.setDisplayName(data.getBandName() + "MagicBand");
            mbm.setLore(Arrays.asList(ChatColor.GREEN + "Click me to open",
                    ChatColor.GREEN + "the MagicBand menu!"));
            mb.setItemMeta(mbm);
        }
        if (pi.getItemInHand().equals(mb)) {
            event.setCancelled(true);
            MagicAssistant.inventoryUtil.openInventory(player, InventoryType.MAINMENU);
        }
    }
}