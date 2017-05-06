package network.palace.parkmanager.listeners;

import network.palace.core.Core;
import network.palace.core.message.FormattedMessage;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.designstation.DesignStation;
import network.palace.parkmanager.handlers.*;
import network.palace.parkmanager.hotels.HotelManager;
import network.palace.parkmanager.utils.WarpUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.List;
import java.util.UUID;

public class PlayerInteract implements Listener {
    public static String disposal = ChatColor.BLUE + "[Disposal]";
    public static String warp = ChatColor.BLUE + "[Warp]";
    public static String hotel = ChatColor.BLUE + "[Hotel]";
    public static String suite = ChatColor.BLUE + "[Suite]";
    public static String designStation = ChatColor.BLUE + "[Design Station]";
    public static String shop = ChatColor.BLUE + "[Shop]";
    public static String queue = ChatColor.BLUE + "[Queue]";
    public static String fastpass = ChatColor.BLUE + "[Fastpass]";
    public static String wait = ChatColor.BLUE + "[Wait Times]";
    public static String mcpro = ChatColor.GREEN + "[MCProHosting]";
    private FormattedMessage mcpromsg = new FormattedMessage("MCProHosting ").color(ChatColor.GREEN)
            .style(ChatColor.BOLD).then("is the ").color(ChatColor.AQUA)
            .then("World Leader in Minecraft Server Hosting! ").color(ChatColor.GREEN).style(ChatColor.ITALIC)
            .then("Click here to purchase a server from MCProHosting!").color(ChatColor.YELLOW)
            .style(ChatColor.UNDERLINE).link("https://palace.network/mcph").tooltip(ChatColor.DARK_AQUA +
                    "Click to purchase a server using Palace's 15%-OFF Discount!");
    private boolean dl = Core.getServerType().equalsIgnoreCase("dlr");

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        CPlayer cp = Core.getPlayerManager().getPlayer(event.getPlayer());
        PlayerData data = ParkManager.getPlayerData(player.getUniqueId());
        Action action = event.getAction();
        Rank rank = Core.getPlayerManager().getPlayer(player.getUniqueId()).getRank();
        if (action.equals(Action.PHYSICAL)) {
            if (dl && rank.getRankId() < Rank.SPECIALGUEST.getRankId()) {
                event.setCancelled(true);
            }
            return;
        }
        final ItemStack hand = player.getInventory().getItemInMainHand();
        if (isArmor(hand)) {
            if (!BlockEdit.isInBuildMode(player.getUniqueId())) {
                event.setCancelled(true);
                player.getInventory().setItemInMainHand(hand);
                ArmorType type = getArmorType(hand);
                PlayerInventory inv = player.getInventory();
                switch (type) {
                    case HELMET:
                        inv.setHelmet(inv.getHelmet());
                        break;
                    case CHESTPLATE:
                        inv.setChestplate(inv.getChestplate());
                        break;
                    case LEGGINGS:
                        inv.setLeggings(inv.getLeggings());
                        break;
                    case BOOTS:
                        inv.setBoots(inv.getBoots());
                        break;
                }
                return;
            }
        }
        if (action.name().toLowerCase().contains("block")) {
            if (player.getInventory().getItemInMainHand().getType().equals(Material.DIAMOND_AXE) &&
                    rank.getRankId() >= Rank.KNIGHT.getRankId()) {
                if (action.equals(Action.LEFT_CLICK_BLOCK)) {
                    event.setCancelled(true);
                    ParkManager.blockChanger.setSelection(0, player, event.getClickedBlock().getLocation());
                } else {
                    event.setCancelled(true);
                    ParkManager.blockChanger.setSelection(1, player, event.getClickedBlock().getLocation());
                }
                return;
            }
        }
        if (action.equals(Action.RIGHT_CLICK_BLOCK) && !(dl && rank.getRankId() < Rank.SPECIALGUEST.getRankId())) {
            Material type = event.getClickedBlock().getType();
            if (type.equals(Material.SIGN) || type.equals(Material.SIGN_POST) || type.equals(Material.WALL_SIGN)) {
                Sign s = (Sign) event.getClickedBlock().getState();
                if (s.getLine(0).equals(disposal)) {
                    player.openInventory(Bukkit.createInventory(player, 36, ChatColor.BLUE + "Disposal"));
                    return;
                }
                if (s.getLine(0).equals(warp)) {
                    Warp warp = WarpUtil.findWarp(ChatColor.stripColor(s.getLine(1)));
                    if (warp == null) {
                        player.sendMessage(ChatColor.RED + "That warp does not exist, sorry!");
                        return;
                    }
                    if (!warp.getServer().equalsIgnoreCase(Core.getInstanceName())) {
                        WarpUtil.crossServerWarp(player.getUniqueId(), warp.getName(), warp.getServer());
                        return;
                    }
                    player.teleport(warp.getLocation());
                    player.sendMessage(ChatColor.BLUE + "You have arrived at "
                            + ChatColor.WHITE + "[" + ChatColor.GREEN + warp.getName()
                            + ChatColor.WHITE + "]");
                    return;
                }
                if (ParkManager.hotelServer) {
                    if (s.getLine(0).equals(hotel) || s.getLine(0).equals(suite)) {
                        boolean suite = s.getLine(0).equals(this.suite);
                        if (suite && rank.getRankId() < Rank.DVCMEMBER.getRankId()) {
                            player.sendMessage(ChatColor.RED + "You must be a " + Rank.DWELLER.getNameWithBrackets()
                                    + ChatColor.RED + " or above to stay in a Suite!");
                            return;
                        }
                        String roomName = ChatColor.stripColor(s.getLine(2)) + " #"
                                + ChatColor.stripColor(s.getLine(1));
                        HotelManager manager = ParkManager.hotelManager;
                        HotelRoom room = manager.getRoom(roomName);
                        if (room == null) {
                            player.sendMessage(ChatColor.RED + "That room is out of service right now, sorry!");
                            return;
                        }
                        if (room.isOccupied() && room.getCurrentOccupant().equals(player.getUniqueId())) {
                            ParkManager.inventoryUtil.openSpecificHotelRoomCheckoutPage(player, room);
                        } else if (room.isOccupied() && !(room.getCurrentOccupant().equals(player.getUniqueId()))) {
                            player.sendMessage(ChatColor.RED + "This room is already occupied!");
                        } else {
                            boolean playerOwnsRooms = false;
                            for (HotelRoom r : manager.getHotelRooms()) {
                                if (r.isOccupied() && r.getCurrentOccupant().equals(player.getUniqueId())) {
                                    playerOwnsRooms = true;
                                    break;
                                }
                            }
                            if (playerOwnsRooms) {
                                player.sendMessage(ChatColor.RED + "You cannot book more than one room at a time! " +
                                        "You need to wait for your current reservation to lapse or check out by " +
                                        "right-clicking the booked room's sign.");
                                return;
                            }

                            ParkManager.inventoryUtil.openSpecificHotelRoomPage(player, manager.getRoom(roomName));
                        }
                        return;
                    }
                }
                if (s.getLine(0).equals(shop)) {
                    String shop = ChatColor.stripColor(s.getLine(1));
                    ParkManager.shopManager.openMenu(cp, shop);
                    return;
                }
                if (s.getLine(0).equals(queue) || s.getLine(0).equals(fastpass) || s.getLine(0).equals(wait)) {
                    ParkManager.queueManager.handle(event);
                    return;
                }
                if (s.getLine(0).equals(mcpro)) {
                    mcpromsg.send(player);
                    return;
                }
                if (Core.getInstanceName().contains("Epcot")) {
                    if (s.getLine(0).equals(designStation)) {
                        if (isInt(ChatColor.stripColor(s.getLine(1)))) {
                            ParkManager.inventoryUtil.openInventory(player, InventoryType.DESIGNSTATION);
                            return;
                        }
                        DesignStation.showStats(player);
                        return;
                    }
                }
            } else if (type.name().toLowerCase().contains("_door") && !type.name().toLowerCase().contains("trap") &&
                    !(dl && rank.getRankId() < Rank.SPECIALGUEST.getRankId())) {
                HotelManager manager = ParkManager.hotelManager;
                HotelRoom room = manager.getRoomFromDoor(event.getClickedBlock(), player);
                if (room != null) {
                    if (room.isOccupied()) {
                        List<UUID> friends = data.getFriendList();
                        if (friends.contains(room.getCurrentOccupant())) {
                            PlayerData target = ParkManager.getPlayerData(room.getCurrentOccupant());
                            if (target == null) {
                                player.sendMessage(ChatColor.RED +
                                        "Your friend must be online for you to access their room!");
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
                                manager.checkout(room, true);
                                return;
                            }
                            return;
                        } else {
                            player.sendMessage(ChatColor.RED + "That room is currently occupied.");
                            event.setCancelled(true);
                        }
                    } else {
                        player.sendMessage(ChatColor.GREEN + "That room is currently unoccupied. Book your stay by " +
                                "right-clicking the sign or viewing the room in your MagicBand.");
                        event.setCancelled(true);
                    }
                }
                return;
            }
        }
        if (BlockEdit.isInBuildMode(player.getUniqueId())) {
            return;
        }
        PlayerInventory pi = player.getInventory();
        if (pi.getHeldItemSlot() == 5) {
            if (pi.getItemInMainHand().getType().equals(Material.CHEST)) {
                event.setCancelled(true);
                ParkManager.inventoryUtil.openInventory(player, InventoryType.BACKPACK);
                return;
            }
        }
        if (pi.getHeldItemSlot() == 6) {
            if (pi.getItemInMainHand().getType().equals(Material.WATCH)) {
                event.setCancelled(true);
                ParkManager.inventoryUtil.openInventory(player, InventoryType.SHOWTIMES);
                return;
            }
        }
        if (pi.getHeldItemSlot() == 8) {
            if (pi.getItemInMainHand().getType().equals(ParkManager.bandUtil.getBandMaterial(data.getBandColor()))) {
                event.setCancelled(true);
                ParkManager.inventoryUtil.openInventory(player, InventoryType.MAINMENU);
                Core.getPlayerManager().getPlayer(player.getUniqueId()).giveAchievement(2);
            }
        }
        if (dl && Core.getPlayerManager().getPlayer(player.getUniqueId()).getRank().getRankId() < Rank.SPECIALGUEST.getRankId()) {
            event.setCancelled(true);
        }
    }

    private ArmorType getArmorType(ItemStack item) {
        String n = item.getType().name().toLowerCase();
        if (n.contains("helmet")) {
            return ArmorType.HELMET;
        }
        if (n.contains("chestplate")) {
            return ArmorType.CHESTPLATE;
        }
        if (n.contains("leggings")) {
            return ArmorType.LEGGINGS;
        }
        if (n.contains("boots")) {
            return ArmorType.BOOTS;
        }
        return null;
    }

    private boolean isArmor(ItemStack item) {
        try {
            String n = item.getType().name().toLowerCase();
            return n.contains("helmet") || n.contains("chestplate") || n.contains("leggings") || n.contains("boots");
        } catch (Exception ignored) {
        }
        return false;
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        EntityType etype = event.getRightClicked().getType();
        if (etype.equals(EntityType.ARMOR_STAND)) {
            ArmorStand stand = (ArmorStand) event.getRightClicked();
            if (!stand.hasMetadata("kiosk")) {
                return;
            }
            event.setCancelled(true);
            if (stand.getMetadata("kiosk").get(0).asBoolean()) {
                ParkManager.fpKioskManager.openKiosk(player);
            }
            return;
        }
        if (!etype.equals(EntityType.ITEM_FRAME)) {
            return;
        }
        CPlayer cplayer = Core.getPlayerManager().getPlayer(player.getUniqueId());
        if (cplayer.getRank().getRankId() < Rank.KNIGHT.getRankId()) {
            if (!BlockEdit.isInBuildMode(player.getUniqueId())) {
                event.setCancelled(true);
            }
        }
    }

    private boolean isInt(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException ignored) {
            return false;
        }
    }
}