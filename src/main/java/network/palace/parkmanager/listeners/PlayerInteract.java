package network.palace.parkmanager.listeners;

import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.handlers.ServerSign;
import network.palace.parkmanager.handlers.magicband.MenuType;
import network.palace.parkmanager.leaderboard.LeaderboardManager;
import network.palace.parkmanager.leaderboard.LeaderboardSign;
import network.palace.parkmanager.magicband.BandInventory;
import network.palace.parkmanager.queues.Queue;
import network.palace.parkwarp.ParkWarp;
import network.palace.parkwarp.handlers.Warp;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PlayerInteract implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        CPlayer player = Core.getPlayerManager().getPlayer(event.getPlayer());
        if (player == null) {
            event.setCancelled(true);
            return;
        }
        Action action = event.getAction();

        //Check sign clicks
        if (action.equals(Action.RIGHT_CLICK_BLOCK)) {
            Block b = event.getClickedBlock();
            if (b.getType().equals(Material.SIGN) || b.getType().equals(Material.WALL_SIGN)) {
                Sign s = (Sign) b.getState();
                ServerSign signType = ServerSign.fromSign(s);
                String[] lines = s.getLines();
                if (signType != null) {
                    switch (signType) {
                        case DISPOSAL:
                            player.openInventory(Bukkit.createInventory(player.getBukkitPlayer(), 36, ChatColor.BLUE + "Disposal"));
                            break;
                        case RIDE_LEADERBOARD:
                            LeaderboardSign leaderboard = ParkManager.getLeaderboardManager().getSign(s.getLocation());
                            if (leaderboard == null) return;
                            String rideName = leaderboard.getRideName();
                            player.sendMessage(ChatColor.AQUA + "Gathering leaderboard data for " + rideName + "...");
                            Core.runTaskAsynchronously(() -> {
                                List<String> messages = new ArrayList<>();
                                for (Map.Entry<UUID, Integer> entry : leaderboard.getCachedMap().entrySet()) {
                                    messages.add(ChatColor.BLUE + LeaderboardManager.getFormattedName(entry.getKey(), entry.getValue()));
                                }
                                LeaderboardManager.sortLeaderboardMessages(messages);
                                player.sendMessage(ChatColor.BLUE + "Ride Counter Leaderboard for " + ChatColor.GOLD + rideName + ":");
                                messages.forEach(player::sendMessage);
                            });
                            return;
                        case SERVER:
                            player.sendToServer(s.getLine(2));
                            return;
                        case WARP:
                            Warp warp = ParkWarp.getWarpUtil().findWarp(ChatColor.stripColor(lines[1]));
                            if (warp == null) {
                                player.sendMessage(ChatColor.RED + "That warp does not exist, sorry!");
                                return;
                            }
                            if (!warp.getServer().equalsIgnoreCase(Core.getInstanceName())) {
                                ParkWarp.getWarpUtil().crossServerWarp(player.getUniqueId(), warp.getName(), warp.getServer());
                                return;
                            }
                            player.teleport(warp.getLocation());
                            player.sendMessage(ChatColor.BLUE + "You have arrived at " + ChatColor.WHITE + "[" +
                                    ChatColor.GREEN + warp.getName() + ChatColor.WHITE + "]");
                            return;
                        case QUEUE:
                            Queue queue = ParkManager.getQueueManager().getQueue(s);
                            if (queue == null) return;
                            if (queue.isInQueue(player)) {
                                queue.leaveQueue(player, false);
                            } else {
                                queue.joinQueue(player);
                            }
                            break;
                    }
                }
                return;
            }
        }

        //Handle inventory item click
        if (ParkManager.getBuildUtil().isInBuildMode(player)) {
            return;
        }
        ItemStack hand = event.getItem();
        if (hand == null || hand.getType() == null) return;
        int slot = player.getHeldItemSlot();
        boolean cancel = false;
        switch (slot) {
            case 5:
                cancel = true;
                ParkManager.getInventoryUtil().openMenu(player, MenuType.BACKPACK);
                break;
            case 6:
                //open watch menu
                ParkManager.getMagicBandManager().openInventory(player, BandInventory.TIMETABLE);
                break;
            case 7:
                //autograph book
                if (!event.getAction().equals(Action.PHYSICAL)) {
                    cancel = true;
                    ParkManager.getAutographManager().handleInteract(player);
                }
                break;
            case 8:
                //open magicband
                ParkManager.getMagicBandManager().openInventory(player, BandInventory.MAIN);
                break;
        }
        if (cancel) event.setCancelled(true);
    }
}
