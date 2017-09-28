package network.palace.parkmanager.listeners;

import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.core.utils.MiscUtil;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.autograph.Signature;
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
import org.bukkit.inventory.meta.BookMeta;

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
    public static String server = ChatColor.BLUE + "[Server]";
    private boolean dl = ParkManager.getInstance().isResort(Resort.DLR);

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        CPlayer cp = Core.getPlayerManager().getPlayer(event.getPlayer());
        if (cp == null) {
            event.setCancelled(true);
            return;
        }
        PlayerData data = ParkManager.getInstance().getPlayerData(cp.getUniqueId());
        Action action = event.getAction();
        Rank rank = Core.getPlayerManager().getPlayer(cp.getUniqueId()).getRank();
        if (action.equals(Action.PHYSICAL)) {
            if (dl && rank.getRankId() < Rank.SPECIALGUEST.getRankId()) {
                event.setCancelled(true);
            }
            return;
        }
        final ItemStack hand = cp.getInventory().getItemInMainHand();
        if (hand.getType().equals(Material.WRITTEN_BOOK)) {
            BookMeta meta = (BookMeta) hand.getItemMeta();
            if (meta.getTitle().startsWith(ParkManager.getInstance().getAutographManager().BOOK_TITLE)) {
                event.setCancelled(true);
                List<Signature> autographs = data.getAutographs();
                ParkManager.getInstance().getAutographManager().openMenu(cp, autographs, player.isSneaking());
                return;
            }
        }
        if (isArmor(hand)) {
            if (!BlockEdit.isInBuildMode(cp.getUniqueId())) {
                event.setCancelled(true);
                cp.getInventory().setItemInMainHand(hand);
                ArmorType type = getArmorType(hand);
                PlayerInventory inv = cp.getInventory();
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
            if (cp.getInventory().getItemInMainHand().getType().equals(Material.DIAMOND_AXE) &&
                    rank.getRankId() >= Rank.KNIGHT.getRankId()) {
                if (action.equals(Action.LEFT_CLICK_BLOCK)) {
                    event.setCancelled(true);
                    ParkManager.getInstance().getBlockChanger().setSelection(0, player, event.getClickedBlock().getLocation());
                } else {
                    event.setCancelled(true);
                    ParkManager.getInstance().getBlockChanger().setSelection(1, player, event.getClickedBlock().getLocation());
                }
                return;
            }
        }
        if (action.equals(Action.RIGHT_CLICK_BLOCK) && !(dl && rank.getRankId() < Rank.SPECIALGUEST.getRankId())) {
            Material type = event.getClickedBlock().getType();
            if (type.equals(Material.SIGN) || type.equals(Material.SIGN_POST) || type.equals(Material.WALL_SIGN)) {
                Sign s = (Sign) event.getClickedBlock().getState();
                if (s.getLine(0).equals(disposal)) {
                    cp.openInventory(Bukkit.createInventory(player, 36, ChatColor.BLUE + "Disposal"));
                    return;
                }
                if (s.getLine(0).equals(warp)) {
                    Warp warp = WarpUtil.findWarp(ChatColor.stripColor(s.getLine(1)));
                    if (warp == null) {
                        cp.sendMessage(ChatColor.RED + "That warp does not exist, sorry!");
                        return;
                    }
                    if (!warp.getServer().equalsIgnoreCase(Core.getInstanceName())) {
                        WarpUtil.crossServerWarp(cp.getUniqueId(), warp.getName(), warp.getServer());
                        return;
                    }
                    cp.teleport(warp.getLocation());
                    cp.sendMessage(ChatColor.BLUE + "You have arrived at "
                            + ChatColor.WHITE + "[" + ChatColor.GREEN + warp.getName()
                            + ChatColor.WHITE + "]");
                    return;
                }
                if (s.getLine(0).equals(shop)) {
                    String shop = ChatColor.stripColor(s.getLine(1));
                    ParkManager.getInstance().getShopManager().openMenu(cp, shop);
                    return;
                }
                if (s.getLine(0).equals(queue) || s.getLine(0).equals(fastpass) || s.getLine(0).equals(wait)) {
                    ParkManager.getInstance().getQueueManager().handle(event);
                    return;
                }
                if (s.getLine(0).equals(server)) {
                    String server = s.getLine(2);
                    Core.getPlayerManager().getPlayer(player).sendToServer(server);
                    return;
                }
                if (Core.getInstanceName().contains("Epcot")) {
                    if (s.getLine(0).equals(designStation)) {
                        if (MiscUtil.checkIfInt(ChatColor.stripColor(s.getLine(1)))) {
                            ParkManager.getInstance().getInventoryUtil().openInventory(player, InventoryType.DESIGNSTATION);
                            return;
                        }
                        DesignStation.showStats(player);
                        return;
                    }
                }
                if (ParkManager.getInstance().isHotelServer()) {
                    if (s.getLine(0).equals(hotel) || s.getLine(0).equals(suite)) {
                        boolean suite = s.getLine(0).equals(this.suite);
                        if (suite && rank.getRankId() < Rank.DVCMEMBER.getRankId()) {
                            cp.sendMessage(ChatColor.RED + "You must be a " + Rank.DWELLER.getFormattedName()
                                    + ChatColor.RED + " or above to stay in a Suite!");
                            return;
                        }
                        String roomName = ChatColor.stripColor(s.getLine(2)) + " #"
                                + ChatColor.stripColor(s.getLine(1));
                        HotelManager manager = ParkManager.getInstance().getHotelManager();
                        HotelRoom room = manager.getRoom(roomName);
                        if (room == null) {
                            cp.sendMessage(ChatColor.RED + "That room is out of service right now, sorry!");
                            return;
                        }
                        if (room.isOccupied() && room.getCurrentOccupant().equals(cp.getUniqueId())) {
                            ParkManager.getInstance().getInventoryUtil().openSpecificHotelRoomCheckoutPage(player, room);
                        } else if (room.isOccupied() && !(room.getCurrentOccupant().equals(cp.getUniqueId()))) {
                            cp.sendMessage(ChatColor.RED + "This room is already occupied!");
                        } else {
                            boolean playerOwnsRooms = false;
                            for (HotelRoom r : manager.getHotelRooms()) {
                                if (r.isOccupied() && r.getCurrentOccupant().equals(cp.getUniqueId())) {
                                    playerOwnsRooms = true;
                                    break;
                                }
                            }
                            if (playerOwnsRooms) {
                                cp.sendMessage(ChatColor.RED + "You cannot book more than one room at a time! " +
                                        "You need to wait for your current reservation to lapse or check out by " +
                                        "right-clicking the booked room's sign.");
                                return;
                            }

                            ParkManager.getInstance().getInventoryUtil().openSpecificHotelRoomPage(player, manager.getRoom(roomName));
                        }
                        return;
                    }
                }
            } else if (type.name().toLowerCase().contains("_door") && !type.name().toLowerCase().contains("trap") &&
                    !(dl && rank.getRankId() < Rank.SPECIALGUEST.getRankId())) {
                HotelManager manager = ParkManager.getInstance().getHotelManager();
                HotelRoom room = manager.getRoomFromDoor(event.getClickedBlock(), player);
                if (room != null) {
                    if (room.isOccupied()) {
                        List<UUID> friends = data.getFriendList();
                        if (friends.contains(room.getCurrentOccupant())) {
                            PlayerData target = ParkManager.getInstance().getPlayerData(room.getCurrentOccupant());
                            if (target == null) {
                                cp.sendMessage(ChatColor.RED +
                                        "Your friend must be online for you to access their room!");
                                event.setCancelled(true);
                                return;
                            }
                            if (!target.isHotel()) {
                                cp.sendMessage(ChatColor.RED + "That room is currently occupied.");
                                event.setCancelled(true);
                            }
                            return;
                        }
                        if (room.getCurrentOccupant().equals(cp.getUniqueId())) {
                            if (room.getCheckoutTime() <= (System.currentTimeMillis() / 1000)) {
                                event.setCancelled(true);
                                manager.checkout(room, true);
                                return;
                            }
                            return;
                        } else {
                            cp.sendMessage(ChatColor.RED + "That room is currently occupied.");
                            event.setCancelled(true);
                        }
                    } else {
                        cp.sendMessage(ChatColor.GREEN + "That room is currently unoccupied. Book your stay by " +
                                "right-clicking the sign or viewing the room in your MagicBand.");
                        event.setCancelled(true);
                    }
                }
                return;
            }
        }
        if (BlockEdit.isInBuildMode(cp.getUniqueId())) {
            return;
        }
        PlayerInventory pi = cp.getInventory();
        if (pi.getHeldItemSlot() == 5) {
            if (pi.getItemInMainHand().getType().equals(Material.CHEST)) {
                event.setCancelled(true);
                ParkManager.getInstance().getInventoryUtil().openInventory(player, InventoryType.BACKPACK);
                return;
            }
        }
        if (pi.getHeldItemSlot() == 6) {
            if (pi.getItemInMainHand().getType().equals(Material.WATCH)) {
                event.setCancelled(true);
                ParkManager.getInstance().getInventoryUtil().openInventory(player, InventoryType.SHOWTIMES);
                return;
            }
        }
        if (pi.getHeldItemSlot() == 8) {
            if (pi.getItemInMainHand().getType().equals(ParkManager.getInstance().getBandUtil().getBandMaterial(data.getBandColor()))) {
                event.setCancelled(true);
                ParkManager.getInstance().getInventoryUtil().openInventory(player, InventoryType.MAINMENU);
                Core.getPlayerManager().getPlayer(cp.getUniqueId()).giveAchievement(2);
            }
        }
        if (dl && Core.getPlayerManager().getPlayer(cp.getUniqueId()).getRank().getRankId() < Rank.SPECIALGUEST.getRankId()) {
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
        CPlayer player = Core.getPlayerManager().getPlayer(event.getPlayer());
        if (player == null) {
            return;
        }
        EntityType etype = event.getRightClicked().getType();
        if (etype.equals(EntityType.ARMOR_STAND)) {
            ArmorStand stand = (ArmorStand) event.getRightClicked();
            if (!stand.hasMetadata("kiosk")) {
                return;
            }
            event.setCancelled(true);
            if (stand.getMetadata("kiosk").get(0).asBoolean()) {
                ParkManager.getInstance().getFpKioskManager().openKiosk(player);
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
}