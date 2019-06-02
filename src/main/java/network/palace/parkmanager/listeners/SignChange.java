package network.palace.parkmanager.listeners;

import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import network.palace.core.utils.MiscUtil;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.handlers.sign.ServerSign;
import network.palace.parkmanager.leaderboard.LeaderboardManager;
import network.palace.parkmanager.leaderboard.LeaderboardSign;
import network.palace.parkmanager.queues.Queue;
import network.palace.parkmanager.queues.QueueSign;
import network.palace.parkmanager.shop.Shop;
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
                Core.runTaskAsynchronously(() -> {
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
                if (!warp.getServer().equalsIgnoreCase(Core.getInstanceName())) {
                    ParkWarp.getWarpUtil().crossServerWarp(player.getUniqueId(), warp.getName(), warp.getServer());
                    return;
                }
                player.teleport(warp.getLocation());
                player.sendMessage(ChatColor.BLUE + "You have arrived at " + ChatColor.WHITE + "[" +
                        ChatColor.GREEN + warp.getName() + ChatColor.WHITE + "]");
            }
        });
        ServerSign.registerSign("[Queue]", new ServerSign.SignHandler() {
            @Override
            public void onSignChange(CPlayer player, SignChangeEvent event) {
                if (!MiscUtil.checkIfInt(event.getLine(1))) {
                    player.sendMessage(ChatColor.RED + "'" + event.getLine(1) + "' is not a queue id, it's not an integer!");
                    return;
                }
                int id = Integer.parseInt(event.getLine(1));
                Queue queue = ParkManager.getQueueManager().getQueue(id);
                if (queue == null) {
                    player.sendMessage(ChatColor.RED + "Couldn't find a queue with id " + id + "!");
                    return;
                }
                queue.addSign(new QueueSign(event.getBlock().getLocation(), queue.getName(), queue.getQueueSize()));
                event.setCancelled(true);
                queue.updateSigns();
            }

            @Override
            public void onInteract(CPlayer player, Sign s, PlayerInteractEvent event) {
                Queue queue = ParkManager.getQueueManager().getQueue(s);
                if (queue == null) return;
                if (queue.isInQueue(player)) {
                    queue.leaveQueue(player, false);
                } else {
                    queue.joinQueue(player);
                }
            }

            @Override
            public void onBreak(CPlayer player, Sign s, BlockBreakEvent event) {
                Queue queue = ParkManager.getQueueManager().getQueue(s);
                if (queue == null) return;
                if (!player.getMainHand().getType().equals(Material.GOLDEN_AXE)) {
                    event.setCancelled(true);
                    player.sendMessage(ChatColor.GREEN + "In order to break a [Queue] sign, you must be holding a "
                            + ChatColor.GOLD + "Golden Axe!");
                    return;
                }
                queue.removeSign(s.getLocation());
                player.sendMessage(ChatColor.GREEN + "You removed a queue sign for " + queue.getName());
            }
        });
        ServerSign.registerSign("[Shop]", new ServerSign.SignHandler() {
            @Override
            public void onSignChange(CPlayer player, SignChangeEvent event) {
                if (!MiscUtil.checkIfInt(event.getLine(1))) {
                    player.sendMessage(ChatColor.RED + "'" + event.getLine(1) + "' is not a shop id, it's not an integer!");
                    return;
                }
                int id = Integer.parseInt(event.getLine(1));
                Shop shop = ParkManager.getShopManager().getShop(id);
                if (shop == null) {
                    player.sendMessage(ChatColor.RED + "Couldn't find a shop with id " + id + "!");
                    return;
                }
                event.setLine(1, shop.getName());
            }

            @Override
            public void onInteract(CPlayer player, Sign s, PlayerInteractEvent event) {
                Shop shop = ParkManager.getShopManager().getShop(ChatColor.stripColor(s.getLine(1)));
                if (shop == null) {
                    player.sendMessage(ChatColor.RED + "Could not find a shop named " + s.getLine(1) + "!");
                    return;
                }
                ParkManager.getShopManager().openShopInventory(player, shop);
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

        /*if (sign != null) {
            event.setLine(0, ChatColor.BLUE + sign.getHeader());
            switch (sign) {
                case DISPOSAL:
                    event.setLine(1, "");
                    event.setLine(2, ChatColor.BLACK + "" + ChatColor.BOLD + "Trash");
                    event.setLine(3, "");
                    break;
                case RIDE_LEADERBOARD:
                    if (!ParkManager.getLeaderboardManager().registerLeaderboardSign(event.getLines(), event.getBlock())) {
                        player.sendMessage(ChatColor.RED + "There was a problem creating that leaderboard sign! This usually happens when a leaderboard sign already exists for this ride.");
                        return;
                    }
                    event.setLine(1, "");
                    event.setLine(2, ChatColor.AQUA + "Updating...");
                    event.setLine(3, "");
                    break;
                case SERVER:
                    String name = event.getLine(1);
                    event.setLine(1, "Click to join");
                    event.setLine(2, name);
                    event.setLine(3, "");
                    break;
                case QUEUE: {
                    if (!MiscUtil.checkIfInt(event.getLine(1))) {
                        player.sendMessage(ChatColor.RED + "'" + event.getLine(1) + "' is not a queue id, it's not an integer!");
                        return;
                    }
                    int id = Integer.parseInt(event.getLine(1));
                    Queue queue = ParkManager.getQueueManager().getQueue(id);
                    if (queue == null) {
                        player.sendMessage(ChatColor.RED + "Couldn't find a queue with id " + id + "!");
                        return;
                    }
                    queue.addSign(new QueueSign(b.getLocation(), queue.getName(), queue.getQueueSize()));
                    event.setCancelled(true);
                    queue.updateSigns();
                    break;
                }
                case SHOP: {
                    if (!MiscUtil.checkIfInt(event.getLine(1))) {
                        player.sendMessage(ChatColor.RED + "'" + event.getLine(1) + "' is not a shop id, it's not an integer!");
                        return;
                    }
                    int id = Integer.parseInt(event.getLine(1));
                    Shop shop = ParkManager.getShopManager().getShop(id);
                    if (shop == null) {
                        player.sendMessage(ChatColor.RED + "Couldn't find a shop with id " + id + "!");
                        return;
                    }
                    event.setLine(1, shop.getName());
                    break;
                }
            }
        }*/

        /*if (b.getType().equals(Material.SIGN)
                || b.getType().equals(Material.SIGN_POST)
                || b.getType().equals(Material.WALL_SIGN)) {
            for (int i = 0; i < 4; i++) {
                event.setLine(i, ChatColor.translateAlternateColorCodes('&', event.getLine(i)));
            }
            String l1 = event.getLine(0);
            if (l1.equalsIgnoreCase("[shop]")) {
                event.setLine(0, PlayerInteract.shop);
                event.setLine(1, ChatColor.DARK_GREEN + event.getLine(1));
                return;
            }
            if (l1.equalsIgnoreCase("[queue]")) {
                ParkManager.getInstance().getQueueManager().createSign(event);
                return;
            }
            if (l1.equalsIgnoreCase("[mural]")) {
                if (event.getLine(1).equalsIgnoreCase("return")) {
                    event.setLine(0, PlayerInteract.mural);
                    event.setLine(1, "Return your");
                    event.setLine(2, "paintbrush here!");
                } else if (event.getLine(1).equalsIgnoreCase("claim")) {
                    event.setLine(0, PlayerInteract.mural);
                    event.setLine(1, "Claim your");
                    event.setLine(2, "paintbrush here!");
                }
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
        }*/
    }
}
