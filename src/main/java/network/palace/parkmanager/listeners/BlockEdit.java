package network.palace.parkmanager.listeners;

import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.handlers.sign.ServerSign;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.HashMap;
import java.util.UUID;

public class BlockEdit implements Listener {
    private HashMap<UUID, Long> delay = new HashMap<>();

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        CPlayer player = Core.getPlayerManager().getPlayer(event.getPlayer().getUniqueId());
        if (player.getRank().getRankId() < Rank.TRAINEEBUILD.getRankId()) {
            event.setCancelled(true);
            return;
        } else if (!ParkManager.getBuildUtil().isInBuildMode(player.getUniqueId())) {
            event.setCancelled(true);

            if (delay.containsKey(player.getUniqueId()) && System.currentTimeMillis() >= delay.get(player.getUniqueId()))
                delay.remove(player.getUniqueId());

            player.sendMessage(ChatColor.RED + "You must be in Build Mode to break blocks!");
            delay.put(player.getUniqueId(), System.currentTimeMillis() + 1000);
            return;
        }

        Material type = event.getBlock().getType();
        if (type.equals(Material.SIGN) || type.equals(Material.WALL_SIGN)) {
            Sign s = (Sign) event.getBlock().getState();
//            ServerSign signType = ServerSign.fromSign(s);

            ServerSign.SignEntry signEntry = ServerSign.getByHeader(s.getLine(0));

            if (signEntry != null) {
                signEntry.getHandler().onBreak(player, s, event);
                /*switch (signType) {
                    case RIDE_LEADERBOARD:
                        ParkManager.getLeaderboardManager().deleteSign(s.getLocation());
                        break;
                    case QUEUE:
                        Queue queue = ParkManager.getQueueManager().getQueue(s);
                        if (queue == null) return;
                        if (!player.getMainHand().getType().equals(Material.GOLDEN_AXE)) {
                            event.setCancelled(true);
                            player.sendMessage(ChatColor.GREEN + "In order to break a " + signType.getSignHeader()
                                    + ChatColor.GREEN + " sign, you must be holding a " + ChatColor.GOLD + "Golden Axe!");
                            return;
                        }
                        queue.removeSign(s.getLocation());
                        player.sendMessage(ChatColor.GREEN + "You removed a queue sign for " + queue.getName());
                        break;
                }*/
            }
        }

    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        CPlayer player = Core.getPlayerManager().getPlayer(event.getPlayer().getUniqueId());
        if (player.getRank().getRankId() < Rank.TRAINEEBUILD.getRankId()) {
            event.setCancelled(true);
        } else if (!ParkManager.getBuildUtil().isInBuildMode(player.getUniqueId())) {
            event.setCancelled(true);

            if (delay.containsKey(player.getUniqueId()) && System.currentTimeMillis() >= delay.get(player.getUniqueId()))
                delay.remove(player.getUniqueId());

            player.sendMessage(ChatColor.RED + "You must be in Build Mode to place blocks!");
            delay.put(player.getUniqueId(), System.currentTimeMillis() + 1000);
        }
    }
}