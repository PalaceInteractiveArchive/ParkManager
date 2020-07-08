package network.palace.parkmanager.listeners;

import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import network.palace.core.utils.TextUtil;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.handlers.Park;
import network.palace.parkmanager.handlers.shop.Shop;
import network.palace.parkmanager.handlers.sign.ServerSign;
import network.palace.parkmanager.leaderboard.LeaderboardManager;
import network.palace.parkmanager.leaderboard.LeaderboardSign;
import network.palace.parkmanager.queues.Queue;
import network.palace.parkmanager.queues.QueueSign;
import network.palace.parkmanager.queues.virtual.VirtualQueue;
import network.palace.parkwarp.ParkWarp;
import network.palace.parkwarp.handlers.Warp;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SignChange implements Listener {

    public SignChange() {
        ServerSign.registerSign("[Disposal]", new ServerSign.SignHandler() {
            @Override
            public void onSignChange(CPlayer player, SignChangeEvent event) {
                event.setLine(1, "");
                event.setLine(2, ChatColor.BLACK + "" + ChatColor.BOLD + "Trash");
                event.setLine(3, "");
            }

            @Override
            public void onInteract(CPlayer player, Sign s, PlayerInteractEvent event) {
                player.openInventory(Bukkit.createInventory(player.getBukkitPlayer(), 36, ChatColor.BLUE + "Disposal"));
            }
        });
        ServerSign.registerSign("[Leaderboard]", new ServerSign.SignHandler() {
            @Override
            public void onSignChange(CPlayer player, SignChangeEvent event) {
                if (!ParkManager.getLeaderboardManager().registerLeaderboardSign(event.getLines(), event.getBlock())) {
                    player.sendMessage(ChatColor.RED + "There was a problem creating that leaderboard sign! This usually happens when a leaderboard sign already exists for this ride.");
                    return;
                }
                event.setLine(1, "");
                event.setLine(2, ChatColor.AQUA + "Updating...");
                event.setLine(3, "");
            }

            @Override
            public void onInteract(CPlayer player, Sign s, PlayerInteractEvent event) {
                LeaderboardSign leaderboard = ParkManager.getLeaderboardManager().getSign(event.getClickedBlock().getLocation());
                if (leaderboard == null) return;
                String rideName = leaderboard.getRideName();
                player.sendMessage(ChatColor.AQUA + "Gathering leaderboard data for " + rideName + "...");
                Core.runTaskAsynchronously(ParkManager.getInstance(), () -> {
                    List<String> messages = new ArrayList<>();
                    for (Map.Entry<UUID, Integer> entry : leaderboard.getCachedMap().entrySet()) {
                        messages.add(ChatColor.BLUE + LeaderboardManager.getFormattedName(entry.getKey(), entry.getValue()));
                    }
                    LeaderboardManager.sortLeaderboardMessages(messages);
                    player.sendMessage(ChatColor.BLUE + "Ride Counter Leaderboard for " + ChatColor.GOLD + rideName + ":");
                    messages.forEach(player::sendMessage);
                });
            }

            @Override
            public void onBreak(CPlayer player, Sign s, BlockBreakEvent event) {
                ParkManager.getLeaderboardManager().deleteSign(s.getLocation());
            }
        });
        ServerSign.registerSign("[Server]", new ServerSign.SignHandler() {
            @Override
            public void onSignChange(CPlayer player, SignChangeEvent event) {
                String name = event.getLine(1);
                event.setLine(1, "Click to join");
                event.setLine(2, name);
                event.setLine(3, "");
            }

            @Override
            public void onInteract(CPlayer player, Sign s, PlayerInteractEvent event) {
                player.sendToServer(s.getLine(2));
            }
        });
        ServerSign.registerSign("[Warp]", new ServerSign.SignHandler() {
            @Override
            public void onInteract(CPlayer player, Sign s, PlayerInteractEvent event) {
                Warp warp = ParkWarp.getWarpUtil().findWarp(ChatColor.stripColor(s.getLine(1)));
                if (warp == null) {
                    player.sendMessage(ChatColor.RED + "That warp does not exist, sorry!");
                    return;
                }
                player.getBukkitPlayer().chat("/warp " + warp.getName());
            }
        });
        ServerSign.registerSign("[Queue]", new ServerSign.SignHandler() {
            @Override
            public void onSignChange(CPlayer player, SignChangeEvent event) {
                Park currentPark = ParkManager.getParkUtil().getPark(event.getBlock().getLocation());
                if (currentPark == null) {
                    player.sendMessage(ChatColor.RED + "This sign must be made within a park!");
                    return;
                }
                String id = event.getLine(1);
                Queue queue = ParkManager.getQueueManager().getQueueById(id, currentPark.getId());
                if (queue == null) {
                    player.sendMessage(ChatColor.RED + "Couldn't find a queue with id " + id + "!");
                    return;
                }
                if (!event.getLine(2).isEmpty()) {
                    if (event.getLine(2).equalsIgnoreCase("wait")) {
                        event.setLine(0, ChatColor.BLUE + "[Wait Times]");
                        event.setLine(1, ChatColor.DARK_AQUA + "Click for the");
                        event.setLine(2, ChatColor.DARK_AQUA + "wait time for");
                        event.setLine(3, ChatColor.DARK_AQUA + queue.getName());
                    } else if (event.getLine(2).equalsIgnoreCase("fp")) {
                        queue.addSign(new QueueSign(event.getBlock().getLocation(), queue.getName(), true, queue.getQueueSize()));
                        event.setCancelled(true);
                        queue.updateSigns();
                    }
                } else {
                    queue.addSign(new QueueSign(event.getBlock().getLocation(), queue.getName(), false, queue.getQueueSize()));
                    event.setCancelled(true);
                    queue.updateSigns();
                }
            }

            @Override
            public void onInteract(CPlayer player, Sign s, PlayerInteractEvent event) {
                Queue queue = ParkManager.getQueueManager().getQueue(s);
                if (queue == null) return;
                if (queue.isInQueue(player)) {
                    queue.leaveQueue(player, false);
                } else if (queue.joinQueue(player)) {
                    ParkManager.getQueueManager().displaySignParticles(player, s);
                }
            }

            @Override
            public void onBreak(CPlayer player, Sign s, BlockBreakEvent event) {
                Queue queue = ParkManager.getQueueManager().getQueue(s);
                if (queue == null) return;
                if (!player.getMainHand().getType().equals(Material.GOLD_AXE)) {
                    event.setCancelled(true);
                    player.sendMessage(ChatColor.GREEN + "In order to break a [Queue] sign, you must be holding a "
                            + ChatColor.GOLD + "Golden Axe!");
                    return;
                }
                queue.removeSign(s.getLocation());
                player.sendMessage(ChatColor.GREEN + "You removed a queue sign for " + queue.getName());
            }
        });
        ServerSign.registerSign("[FastPass]", new ServerSign.SignHandler() {
            @Override
            public void onInteract(CPlayer player, Sign s, PlayerInteractEvent event) {
                Queue queue = ParkManager.getQueueManager().getQueue(s);
                if (queue == null) return;
                if (queue.isInQueue(player)) {
                    queue.leaveQueue(player, false);
                } else if (queue.joinFastPassQueue(player)) {
                    ParkManager.getQueueManager().displaySignParticles(player, s);
                }
            }

            @Override
            public void onBreak(CPlayer player, Sign s, BlockBreakEvent event) {
                Queue queue = ParkManager.getQueueManager().getQueue(s);
                if (queue == null) return;
                if (!player.getMainHand().getType().equals(Material.GOLD_AXE)) {
                    event.setCancelled(true);
                    player.sendMessage(ChatColor.GREEN + "In order to break a [FastPass] sign, you must be holding a "
                            + ChatColor.GOLD + "Golden Axe!");
                    return;
                }
                queue.removeSign(s.getLocation());
                player.sendMessage(ChatColor.GREEN + "You removed a queue sign for " + queue.getName());
            }
        });
        ServerSign.registerSign("[Wait Times]", new ServerSign.SignHandler() {
            @Override
            public void onInteract(CPlayer player, Sign s, PlayerInteractEvent event) {
                Park currentPark = ParkManager.getParkUtil().getPark(event.getClickedBlock().getLocation());
                if (currentPark == null) {
                    player.sendMessage(ChatColor.RED + "This sign must be used within a park!");
                    return;
                }
                Queue queue = ParkManager.getQueueManager().getQueueByName(s.getLine(3), currentPark.getId());
                if (queue == null) return;
                if (!queue.isOpen()) {
                    player.sendMessage(ChatColor.GREEN + "This queue is currently " + ChatColor.RED + "closed!");
                } else {
                    String wait = queue.getWaitFor(player.getUniqueId());
                    player.sendMessage(ChatColor.GREEN + "The estimated wait time for " + queue.getName() +
                            ChatColor.GREEN + " is " + ChatColor.AQUA + wait);
                }
            }

            @Override
            public void onBreak(CPlayer player, Sign s, BlockBreakEvent event) {
                Park currentPark = ParkManager.getParkUtil().getPark(event.getBlock().getLocation());
                if (currentPark == null) return;
                Queue queue = ParkManager.getQueueManager().getQueueByName(s.getLine(3), currentPark.getId());
                if (queue == null) return;
                if (!player.getMainHand().getType().equals(Material.GOLD_AXE)) {
                    event.setCancelled(true);
                    player.sendMessage(ChatColor.GREEN + "In order to break a [Wait Times] sign, you must be holding a "
                            + ChatColor.GOLD + "Golden Axe!");
                    return;
                }
                player.sendMessage(ChatColor.GREEN + "You removed a wait time sign for " + queue.getName());
            }
        });
        ServerSign.registerSign("[Shop]", new ServerSign.SignHandler() {
            @Override
            public void onSignChange(CPlayer player, SignChangeEvent event) {
                Park currentPark = ParkManager.getParkUtil().getPark(event.getBlock().getLocation());
                if (currentPark == null) {
                    player.sendMessage(ChatColor.RED + "This sign must be used within a park!");
                    return;
                }
                String id = event.getLine(1);
                Shop shop = ParkManager.getShopManager().getShopById(id, currentPark.getId());
                if (shop == null) {
                    player.sendMessage(ChatColor.RED + "Couldn't find a shop with id " + id + "!");
                    return;
                }
                event.setLine(1, shop.getName());
            }

            @Override
            public void onInteract(CPlayer player, Sign s, PlayerInteractEvent event) {
                Park currentPark = ParkManager.getParkUtil().getPark(event.getClickedBlock().getLocation());
                if (currentPark == null) {
                    player.sendMessage(ChatColor.RED + "This sign must be used within a park!");
                    return;
                }
                Shop shop = ParkManager.getShopManager().getShopByName(ChatColor.stripColor(s.getLine(1)), currentPark.getId());
                if (shop == null) {
                    player.sendMessage(ChatColor.RED + "Could not find a shop named " + s.getLine(1) + "!");
                    return;
                }
                ParkManager.getShopManager().openShopInventory(player, shop);
            }
        });
        ServerSign.registerSign("[vqueue]", new ServerSign.SignHandler() {
            @Override
            public void onSignChange(CPlayer player, SignChangeEvent event) {
                event.setLine(0, ChatColor.AQUA + "[Virtual Queue]");
                String id = event.getLine(1);
                VirtualQueue queue = ParkManager.getVirtualQueueManager().getQueueById(id);
                if (queue == null) {
                    player.sendMessage(ChatColor.RED + "Couldn't find a queue with id " + id + "!");
                    return;
                }
                event.setLine(1, ChatColor.BLUE + id);
                event.setLine(3, ChatColor.YELLOW + "" + queue.getMembers().size() + " Player" +
                        TextUtil.pluralize(queue.getMembers().size()));
                if (event.getLine(2).equalsIgnoreCase("advance")) {
                    event.setLine(2, ChatColor.YELLOW + "" + ChatColor.BOLD + "Advance");
                    queue.setAdvanceSign((Sign) event.getBlock().getState());
                } else if (event.getLine(2).equalsIgnoreCase("state")) {
                    event.setLine(2, (queue.isOpen() ? ChatColor.GREEN : ChatColor.RED) + "" + ChatColor.BOLD +
                            (queue.isOpen() ? "Open" : "Closed"));
                    queue.setStateSign((Sign) event.getBlock().getState());
                }
            }
        });
        ServerSign.registerSign("[Virtual Queue]", new ServerSign.SignHandler() {
            @Override
            public void onInteract(CPlayer player, Sign s, PlayerInteractEvent event) {
                VirtualQueue queue = ParkManager.getVirtualQueueManager().getQueue(s);
                if (queue == null) return;
                if (s.getLine(2).startsWith(ChatColor.YELLOW.toString())) {
                    player.performCommand("vq advance " + queue.getId());
                } else {
                    player.performCommand("vq " + (queue.isOpen() ? "close " : "open ") + queue.getId());
                }
            }

            @Override
            public void onBreak(CPlayer player, Sign s, BlockBreakEvent event) {
                VirtualQueue queue = ParkManager.getVirtualQueueManager().getQueue(s);
                if (queue == null) return;
                event.setCancelled(true);
                player.sendMessage(ChatColor.AQUA + "You can only destroy this sign once the queue is removed!");
            }
        });
    }

    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        CPlayer player = Core.getPlayerManager().getPlayer(event.getPlayer());
        if (player == null) return;
        Block b = event.getBlock();

        for (int i = 0; i < 4; i++) {
            event.setLine(i, ChatColor.translateAlternateColorCodes('&', event.getLine(i)));
        }
        String line1 = event.getLine(0);

        ServerSign.SignEntry signEntry = ServerSign.getByHeader(line1);

        if (signEntry != null) {
            event.setLine(0, ChatColor.BLUE + signEntry.getHeader());
            signEntry.getHandler().onSignChange(player, event);
        }
    }
}
