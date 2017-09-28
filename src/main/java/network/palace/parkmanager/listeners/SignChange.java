package network.palace.parkmanager.listeners;

import network.palace.core.Core;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.handlers.HotelRoom;
import network.palace.parkmanager.handlers.Warp;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

public class SignChange implements Listener {

    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        Player player = event.getPlayer();
        Block b = event.getBlock();
        if (b.getType().equals(Material.SIGN)
                || b.getType().equals(Material.SIGN_POST)
                || b.getType().equals(Material.WALL_SIGN)) {
            for (int i = 0; i < 4; i++) {
                event.setLine(i, ChatColor.translateAlternateColorCodes('&', event.getLine(i)));
            }
            String l1 = event.getLine(0);
            if (l1.equalsIgnoreCase("[warp]")) {
                event.setLine(0, PlayerInteract.warp);
                return;
            }
            if (l1.equalsIgnoreCase("[disposal]")) {
                event.setLine(0, PlayerInteract.disposal);
                event.setLine(1, "");
                event.setLine(2, ChatColor.BLACK + "" + ChatColor.BOLD + "Trash");
                event.setLine(3, "");
                return;
            }
            if (l1.equalsIgnoreCase("[server]")) {
                String name = event.getLine(1);
                event.setLine(0, PlayerInteract.server);
                event.setLine(1, "Click to join");
                event.setLine(2, name);
                event.setLine(3, "");
                return;
            }
            if (l1.equalsIgnoreCase("[hotel]") || l1.equalsIgnoreCase("[suite]")) {
                if (!ParkManager.getInstance().isHotelServer()) {
                    return;
                }
                boolean suite = l1.equalsIgnoreCase("[suite]");
                event.setLine(0, suite ? PlayerInteract.suite : PlayerInteract.hotel);
                int roomNumber = Integer.parseInt(ChatColor.stripColor(event.getLine(1)));
                String hotelName = ChatColor.stripColor(event.getLine(2));
                int cost = Integer.parseInt(ChatColor.stripColor(event.getLine(3).replace("$", "")));
                String fullRoomName = hotelName + " #" + ChatColor.stripColor(event.getLine(1));
                if (ParkManager.getInstance().getHotelManager().getRoom(fullRoomName) == null) {
                    Location loc = b.getLocation();
                    HotelRoom newRoom = new HotelRoom(hotelName, roomNumber, null, "", 0, null, cost, null, 259200,
                            loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), suite);
                    event.setLine(1, "" + ChatColor.GOLD + newRoom.getRoomNumber());
                    event.setLine(2, ChatColor.DARK_GREEN + event.getLine(2));
                    event.setLine(3, "" + ChatColor.GREEN + cost);
                    Location loc2 = ParkManager.getInstance().getHotelManager().locFromSign((Sign) b.getState());
                    Warp warp = null;
                    if (loc != null) {
                        warp = new Warp(newRoom.getName().replace(" ", ""), Core.getInstanceName(),
                                loc2.getX(), loc2.getY(), loc2.getZ(), loc2.getYaw(), loc2.getPitch(),
                                player.getWorld().getName());
                    }
                    newRoom.setWarp(warp);
                    ParkManager.getInstance().getHotelManager().addRoom(newRoom);
                    ParkManager.getInstance().getHotelManager().updateRooms();
                }
                return;
            }
            if (l1.equalsIgnoreCase("[shop]")) {
                event.setLine(0, PlayerInteract.shop);
                event.setLine(1, ChatColor.DARK_GREEN + event.getLine(1));
                return;
            }
            if (l1.equalsIgnoreCase("[queue]")) {
                ParkManager.getInstance().getQueueManager().createSign(event);
                return;
            }
            if (Core.getInstanceName().contains("Epcot")) {
                if (l1.equalsIgnoreCase("[design station]")) {
                    if (event.getLine(1).equalsIgnoreCase("stats")) {
                        event.setLine(0, PlayerInteract.designStation);
                        event.setLine(1, ChatColor.GOLD + "Stats");
                        event.setLine(2, ChatColor.DARK_GREEN + "Test Track");
                    } else {
                        event.setLine(0, PlayerInteract.designStation);
                        event.setLine(1, ChatColor.GOLD + event.getLine(1));
                        event.setLine(2, ChatColor.DARK_GREEN + "Test Track");
                    }
                }
            }
        }
    }
}