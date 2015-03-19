package us.mcmagic.magicassistant.listeners;

import org.bukkit.*;
import org.bukkit.block.Block;
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
import us.mcmagic.magicassistant.utils.*;
import us.mcmagic.mcmagiccore.MCMagicCore;
import us.mcmagic.mcmagiccore.permissions.Rank;

import java.util.Arrays;

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
        if (event.getAction().equals(Action.PHYSICAL)) {
            return;
        }
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
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
                    if (room.isOccupied() && room.getCurrentOccupant().equalsIgnoreCase(player.getUniqueId().toString())) {
                        InventoryUtil.openSpecificHotelRoomCheckoutPage(player, HotelUtil.getRoom(roomName));
                    } else if (room.isOccupied() && !(room.getCurrentOccupant().equalsIgnoreCase(player.getUniqueId().toString()))) {
                        player.sendMessage(ChatColor.RED + "This room is already occupied!");
                    } else {
                        boolean playerOwnsRooms = false;
                        for (HotelRoom r : HotelUtil.getRooms()) {
                            if (r.isOccupied() && r.getCurrentOccupant().equalsIgnoreCase(player.getUniqueId().toString())) {
                                playerOwnsRooms = true;
                                break;
                            }
                        }
                        if (playerOwnsRooms) {
                            player.sendMessage(ChatColor.RED + "You cannot book more than one room at a time!  You need to wait for your current reservation to lapse or check out by right-clicking the booked room's sign.");
                            return;
                        }

                        InventoryUtil.openSpecificHotelRoomPage(player, HotelUtil.getRoom(roomName));
                    }
                    return;
                }
            } else if (type == Material.WOODEN_DOOR || type == Material.BIRCH_DOOR ||
                    type == Material.SPRUCE_DOOR || type == Material.JUNGLE_DOOR ||
                    type == Material.DARK_OAK_DOOR || type == Material.ACACIA_DOOR) {
                boolean isCM = false;
                //This try-catch can be removed at an undetermined point in the future.
                try {
                    isCM = MCMagicCore.getUser(player.getUniqueId()).getRank().getRankId() >= Rank.CASTMEMBER.getRankId();
                } catch (Exception nsme) {
                    if (player.isOp()) {
                        isCM = true;
                        player.sendMessage(ChatColor.RED + "The MCMagicCore plugin needs to be updated on this server!");
                        player.sendMessage(ChatColor.RED + "If you are not a technician, please notify one that you recieved this message.");
                    }
                }
                HotelRoom room = getRoomFromDoor(event.getClickedBlock(), player);
                if (room != null) {
                    if (room.isOccupied()) {
                        if (room.getCurrentOccupant().equalsIgnoreCase(player.getUniqueId().toString())) {
                            return;
                        } else {
                            player.sendMessage(ChatColor.RED + "That room is currently occupied.");
                            event.setCancelled(!isCM);
                        }
                    } else {
                        player.sendMessage(ChatColor.GREEN + "That room is currently unoccupied.  Book your stay by right-clicking the sign or viewing the room in your MagicBand.");
                        event.setCancelled(!isCM);
                    }
                }
                return;
            } else if (type == Material.SKULL) {
                Skull skull = (Skull) event.getClickedBlock().getState();
                if (skull.getSkullType() == SkullType.PLAYER) {
                    if (skull.getOwner().equalsIgnoreCase("Telephone") || skull.getOwner().equalsIgnoreCase("a15f8f85-3f97-4c4e-a61d-43b5ad6aafec")) {
                        player.sendMessage(ChatColor.RED + "You cannot use hotel room telephones yet!");
                        return;
                    }
                }
                return;
            }
        }
        PlayerInventory pi = player.getInventory();
        if (pi.getHeldItemSlot() != 8) {
            return;
        }
        if (BandUtil.isLoading(player)) {
            player.sendMessage(ChatColor.GRAY + "Your MagicBand is currently initializing!");
            return;
        }
        ItemStack mb;
        if (data.getSpecial()) {
            mb = new ItemStack(BandUtil.getBandMaterial(data.getBandColor()));
            ItemMeta mbm = mb.getItemMeta();
            mbm.setDisplayName(data.getBandName() + "MagicBand");
            mbm.setLore(Arrays.asList(ChatColor.GREEN + "Click me to open",
                    ChatColor.GREEN + "the MagicBand menu!"));
            mb.setItemMeta(mbm);
        } else {
            mb = new ItemStack(Material.FIREWORK_CHARGE);
            FireworkEffectMeta mbm = (FireworkEffectMeta) mb.getItemMeta();
            mbm.setEffect(FireworkEffect.builder().withColor(BandUtil.getBandColor(data.getBandColor())).build());
            mbm.setDisplayName(data.getBandName() + "MagicBand");
            mbm.setLore(Arrays.asList(ChatColor.GREEN + "Click me to open",
                    ChatColor.GREEN + "the MagicBand menu!"));
            mb.setItemMeta(mbm);
        }
        if (pi.getItemInHand().equals(mb)) {
            event.setCancelled(true);
            InventoryUtil.openInventory(player, InventoryType.MAINMENU);
        }
    }

    public HotelRoom getRoomFromDoor(Block b, Player p) {
        for (int ix = b.getLocation().getBlockX() - 1; ix < b.getLocation().getBlockX() + 2; ix++) {
            for (int iz = b.getLocation().getBlockZ() - 1; iz < b.getLocation().getBlockZ() + 2; iz++) {
                for (int iy = b.getLocation().getBlockY() - 1; iy < b.getLocation().getBlockY() + 2; iy++) {
                    Block targetBlock = b.getWorld().getBlockAt(ix, iy, iz);
                    Material type = targetBlock.getType();
                    if (type == Material.SIGN_POST || type == Material.WALL_SIGN) {
                        Sign s = (Sign)targetBlock.getState();
                        HotelRoom room = getRoomFromSign(s);
                        if (room != null) {
                            return room;
                        }
                    }
                }
            }
        }
        return null;
    }

    public HotelRoom getRoomFromSign(Sign s) {
        if (!ChatColor.stripColor(s.getLine(0)).equalsIgnoreCase("[hotel]") || s.getLine(1).equals("") || s.getLine(2).equals("")) {
            return null;
        }
        return HotelUtil.getRoom(ChatColor.stripColor(s.getLine(2)) + " #" + ChatColor.stripColor(s.getLine(1)));
    }

}