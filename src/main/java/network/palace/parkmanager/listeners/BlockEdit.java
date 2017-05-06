package network.palace.parkmanager.listeners;

import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.hotels.HotelManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class BlockEdit implements Listener {
    private static List<UUID> buildMode = new ArrayList<>();
    private HashMap<UUID, Long> delay = new HashMap<>();

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        CPlayer user = Core.getPlayerManager().getPlayer(player.getUniqueId());
        if (user.getRank().getRankId() < Rank.KNIGHT.getRankId()) {
            event.setCancelled(true);
            return;
        } else {
            if (!isInBuildMode(player.getUniqueId())) {
                event.setCancelled(true);
                if (delay.containsKey(player.getUniqueId())) {
                    if (System.currentTimeMillis() < delay.get(player.getUniqueId())) {
                        return;
                    }
                    delay.remove(player.getUniqueId());
                }
                player.sendMessage(ChatColor.RED + "You must be in Build Mode to break blocks!");
                delay.put(player.getUniqueId(), System.currentTimeMillis() + 1000);
                return;
            }
        }
        if (event.getBlock().getType() == Material.SIGN_POST || event.getBlock().getType() == Material.WALL_SIGN) {
            Sign s = (Sign) event.getBlock().getState();
            String l1 = ChatColor.stripColor(s.getLine(0));
            if (l1.equalsIgnoreCase("[hotel]")) {
                if (!player.getInventory().getItemInMainHand().getType().equals(Material.STICK)) {
                    event.setCancelled(true);
                    player.sendMessage(ChatColor.RED + "Please break " + ChatColor.BLUE + "[Hotel] " + ChatColor.RED +
                            "signs with a " + ChatColor.GREEN + "Stick.");
                    return;
                }
                String hotelName = ChatColor.stripColor(s.getLine(2));
                String fullRoomName = hotelName + " #" + ChatColor.stripColor(s.getLine(1));
                HotelManager manager = ParkManager.hotelManager;
                if (manager.getRoom(fullRoomName) != null) {
                    manager.removeRoom(manager.getRoom(fullRoomName));
                    manager.refreshRooms();
                    manager.updateRooms();
                }
                return;
            }
            if (l1.equalsIgnoreCase("[queue]")) {
                try {
                    ParkManager.queueManager.deleteSign(s.getLocation());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (l1.equalsIgnoreCase("[fastpass]")) {
                try {
                    ParkManager.queueManager.deleteFPSign(s.getLocation());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        CPlayer cplayer = Core.getPlayerManager().getPlayer(player.getUniqueId());
        if (event.getBlockPlaced().getType().equals(Material.THIN_GLASS)) {
            Location loc = player.getLocation();
            if (loc.getBlock().getType().equals(Material.AIR) && (loc.clone().add(0, -0.5, 0).getBlock()
                    .getType().equals(Material.THIN_GLASS) && loc.clone().add(0, -0.5, 0).getBlock()
                    .getLocation().equals(event.getBlockPlaced().getLocation()))) {
                Location loc2 = loc.clone().add(0, -1, 0);
                if (loc2.getBlock().getType().isSolid()) {
                    Location target = new Location(loc2.getWorld(), loc2.getX(), loc2.getBlock().getLocation().clone()
                            .add(0, 1, 0).getY(), loc2.getZ(), loc2.getYaw(), loc2.getPitch());
                    player.teleport(target);
                }
            }
        }

        if (cplayer.getRank().getRankId() < Rank.KNIGHT.getRankId()) {
            event.setCancelled(true);
        } else {
            if (!isInBuildMode(player.getUniqueId())) {
                event.setCancelled(true);
                if (delay.containsKey(player.getUniqueId())) {
                    if (System.currentTimeMillis() < delay.get(player.getUniqueId())) {
                        return;
                    }
                    delay.remove(player.getUniqueId());
                }
                player.sendMessage(ChatColor.RED + "You must be in Build Mode to place blocks!");
                delay.put(player.getUniqueId(), System.currentTimeMillis() + 1000);
            }
        }
    }

    public static boolean isInBuildMode(UUID uuid) {
        return buildMode.contains(uuid);
    }

    public static boolean toggleBuildMode(UUID uuid) {
        if (buildMode.contains(uuid)) {
            buildMode.remove(uuid);
            return false;
        } else {
            buildMode.add(uuid);
            return true;
        }
    }

    public static void logout(UUID uuid) {
        buildMode.remove(uuid);
    }
}