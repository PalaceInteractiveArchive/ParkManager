package us.mcmagic.magicassistant.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import us.mcmagic.magicassistant.MagicAssistant;
import us.mcmagic.magicassistant.hotels.HotelManager;
import us.mcmagic.mcmagiccore.MCMagicCore;
import us.mcmagic.mcmagiccore.permissions.Rank;
import us.mcmagic.mcmagiccore.player.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BlockEdit implements Listener {
    private static List<UUID> buildMode = new ArrayList<>();

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        User user = MCMagicCore.getUser(player.getUniqueId());
        if (user.getRank().getRankId() < Rank.CASTMEMBER.getRankId()) {
            event.setCancelled(true);
            return;
        } else {
            if (!isInBuildMode(player.getUniqueId())) {
                player.sendMessage(ChatColor.RED + "You must be in Build Mode to break blocks!");
                event.setCancelled(true);
                return;
            }
        }
        if (event.getBlock().getType() == Material.SIGN_POST || event.getBlock().getType() == Material.WALL_SIGN) {
            Sign s = (Sign) event.getBlock().getState();
            String l1 = ChatColor.stripColor(s.getLine(0));
            if (l1.equalsIgnoreCase("[hotel]")) {
                if (!player.getItemInHand().getType().equals(Material.STICK)) {
                    event.setCancelled(true);
                    player.sendMessage(ChatColor.RED + "Please break " + ChatColor.BLUE + "[Hotel] " + ChatColor.RED +
                            "signs with a " + ChatColor.GREEN + "Stick.");
                    return;
                }
                String hotelName = ChatColor.stripColor(s.getLine(2));
                String fullRoomName = hotelName + " #" + ChatColor.stripColor(s.getLine(1));
                HotelManager manager = MagicAssistant.hotelManager;
                if (manager.getRoom(fullRoomName) != null) {
                    manager.removeRoom(manager.getRoom(fullRoomName));
                    manager.refreshRooms();
                    manager.updateRooms();
                }
                return;
            }
            if (l1.equalsIgnoreCase("[queue]")) {
                try {
                    MagicAssistant.queueManager.deleteSign(s.getLocation());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (l1.equalsIgnoreCase("[fastpass]")) {
                try {
                    MagicAssistant.queueManager.deleteFPSign(s.getLocation());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        User user = MCMagicCore.getUser(player.getUniqueId());
        if (user.getRank().getRankId() < Rank.CASTMEMBER.getRankId()) {
            event.setCancelled(true);
        } else {
            if (!isInBuildMode(player.getUniqueId())) {
                player.sendMessage(ChatColor.RED + "You must be in Build Mode to place blocks!");
                event.setCancelled(true);
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