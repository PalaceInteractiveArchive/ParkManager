package us.mcmagic.magicassistant.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import us.mcmagic.magicassistant.MagicAssistant;
import us.mcmagic.magicassistant.handlers.HotelRoom;
import us.mcmagic.magicassistant.handlers.Warp;
import us.mcmagic.mcmagiccore.MCMagicCore;

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
            if (event.getLine(0).equalsIgnoreCase("[warp]")) {
                event.setLine(0, PlayerInteract.warp);
                return;
            }
            if (event.getLine(0).equalsIgnoreCase("[disposal]")) {
                event.setLine(0, PlayerInteract.disposal);
                return;
            }
            if (event.getLine(0).equalsIgnoreCase("[hotel]")) {
                event.setLine(0, PlayerInteract.hotel);
                int roomNumber = Integer.parseInt(ChatColor.stripColor(event.getLine(1)));
                String hotelName = ChatColor.stripColor(event.getLine(2));
                int cost = Integer.parseInt(ChatColor.stripColor(event.getLine(3).replace(" Coins", "").replace(" Coin", "").replace("$", "")));
                String fullRoomName = hotelName + " #" + ChatColor.stripColor(event.getLine(1));
                if (MagicAssistant.hotelManager.getRoom(fullRoomName) == null) {
                    Location loc = b.getLocation();
                    HotelRoom newRoom = new HotelRoom(hotelName, roomNumber, null, "", 0, null, cost, null, 259200,
                            loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
                    event.setLine(1, "" + ChatColor.GOLD + newRoom.getRoomNumber());
                    event.setLine(2, ChatColor.DARK_GREEN + event.getLine(2));
                    event.setLine(3, "" + ChatColor.GREEN + cost);
                    Location loc2 = MagicAssistant.hotelManager.locFromSign(((Sign) b.getState()));
                    Warp warp = null;
                    if (loc != null) {
                        warp = new Warp(newRoom.getName().replace(" ", ""), MCMagicCore.getMCMagicConfig().serverName,
                                loc2.getX(), loc2.getY(), loc2.getZ(), loc2.getYaw(), loc2.getPitch(),
                                player.getWorld().getName());
                    }
                    newRoom.setWarp(warp);
                    MagicAssistant.hotelManager.addRoom(newRoom);
                    MagicAssistant.hotelManager.updateRooms();
                }
                return;
            }
            if (event.getLine(0).equalsIgnoreCase("[shop]")) {
                event.setLine(0, PlayerInteract.shop);
                event.setLine(1, ChatColor.DARK_GREEN + event.getLine(1));
                return;
            }
            if (MCMagicCore.getMCMagicConfig().serverName.contains("Epcot")) {
                if (event.getLine(0).equalsIgnoreCase("[design station]")) {
                    event.setLine(0, PlayerInteract.designStation);
                    event.setLine(1, ChatColor.GOLD + event.getLine(1));
                    event.setLine(2, ChatColor.DARK_GREEN + "Test Track");
                }
            }
        }
    }
}