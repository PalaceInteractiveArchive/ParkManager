package us.mcmagic.magicassistant.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.mcmagic.magicassistant.MagicAssistant;
import us.mcmagic.magicassistant.handlers.Warp;
import us.mcmagic.magicassistant.utils.PlayerUtil;
import us.mcmagic.magicassistant.utils.WarpUtil;
import us.mcmagic.mcmagiccore.MCMagicCore;
import us.mcmagic.mcmagiccore.permissions.Rank;

import java.util.List;

public class Command_warp {

    public static void execute(String label, CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            if (args.length == 2) {
                if (!sender.hasPermission("mcmagic.warp.others")) {
                    sender.sendMessage(ChatColor.RED + "You do not have permission to warp others!");
                    return;
                }
                if (PlayerUtil.findPlayer(args[1]) == null) {
                    sender.sendMessage(ChatColor.RED + "Player not found.");
                    return;
                }
                final Player tp = PlayerUtil.findPlayer(args[1]);
                final String w = args[0];
                Warp warp;
                if (WarpUtil.findWarp(w) == null) {
                    sender.sendMessage(ChatColor.RED + "Warp not found!");
                    return;
                } else {
                    warp = WarpUtil.findWarp(w);
                }
                String targetServer = warp.getServer();
                String currentServer = MagicAssistant.serverName;
                final Location loc = warp.getLocation();
                if (targetServer.equals(currentServer)) {
                    if (tp.isInsideVehicle()) {
                        tp.getVehicle().eject();
                        Bukkit.getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugin("MagicAssistant"), new Runnable() {
                            @Override
                            public void run() {
                                tp.teleport(loc);
                                tp.sendMessage(ChatColor.BLUE + "You have arrived at "
                                        + ChatColor.WHITE + "[" + ChatColor.GREEN + w
                                        + ChatColor.WHITE + "]");
                            }
                        }, 10L);
                        return;
                    }
                    tp.teleport(warp.getLocation());
                    tp.sendMessage(ChatColor.BLUE + "You have arrived at "
                            + ChatColor.WHITE + "[" + ChatColor.GREEN + w
                            + ChatColor.WHITE + "]");
                    sender.sendMessage(ChatColor.BLUE + tp.getName()
                            + " has arrived at " + ChatColor.WHITE + "["
                            + ChatColor.GREEN + w + ChatColor.WHITE + "]");
                    return;
                } else {
                    WarpUtil.crossServerWarp(tp.getUniqueId() + "", w,
                            targetServer);
                    return;
                }
            }
            sender.sendMessage(ChatColor.RED + "/warp [Warp Name] [Username]");
            return;
        }
        final Player player = (Player) sender;
        if (args.length == 1) {
            final String w = args[0];
            Warp warp;
            if (WarpUtil.findWarp(w) == null) {
                player.sendMessage(ChatColor.RED + "Warp not found!");
                return;
            } else {
                warp = WarpUtil.findWarp(w);
            }
            Rank rank = MCMagicCore.getUser(player.getUniqueId()).getRank();
            if (warp.getName().toLowerCase().startsWith("dvc")) {
                if (rank.getRankId() < Rank.DVCMEMBER.getRankId()) {
                    player.sendMessage(ChatColor.RED + "You must be the " + Rank.DVCMEMBER.getNameWithBrackets()
                            + ChatColor.RED + " rank or above to use this warp!");
                    return;
                }
            }
            if (warp.getName().toLowerCase().startsWith("char")) {
                if (rank.getRankId() < Rank.CHARACTERGUEST.getRankId()) {
                    player.sendMessage(ChatColor.RED + "You must be the " + Rank.CHARACTERGUEST.getNameWithBrackets()
                            + ChatColor.RED + " rank or above to use this warp!");
                    return;
                }
            }
            if (warp.getName().toLowerCase().startsWith("staff")) {
                if (rank.getRankId() < Rank.INTERN.getRankId()) {
                    player.sendMessage(ChatColor.RED + "You must be the " + Rank.INTERN.getNameWithBrackets()
                            + ChatColor.RED + " rank or above to use this warp!");
                    return;
                }
            }
            if (warp.getName().toLowerCase().startsWith("staff")) {
                if (!(rank.getOp())) {
                    player.sendMessage(ChatColor.RED + "You must be the " + Rank.CASTMEMBER.getNameWithBrackets() + ChatColor.RED + " rank or above to use this warp!");
                    return;
                }
            }
            if (warp.getName().toLowerCase().startsWith("intern")) {
                if (!(rank.getOp() || rank.equals(Rank.INTERN))) {
                    player.sendMessage(ChatColor.RED + "You must be the " + Rank.INTERN.getNameWithBrackets() + ChatColor.RED + " rank or above to use this warp!");
                    return;
                }

            }
            String targetServer = warp.getServer();
            String currentServer = MagicAssistant.serverName;
            final Location loc = warp.getLocation();
            if (targetServer.equals(currentServer)) {
                if (player.isInsideVehicle()) {
                    player.getVehicle().eject();
                    Bukkit.getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugin("MagicAssistant"), new Runnable() {
                        @Override
                        public void run() {
                            player.teleport(loc);
                            player.sendMessage(ChatColor.BLUE + "You have arrived at "
                                    + ChatColor.WHITE + "[" + ChatColor.GREEN + w
                                    + ChatColor.WHITE + "]");
                        }
                    }, 10L);
                    return;
                }
                player.teleport(warp.getLocation());
                player.sendMessage(ChatColor.BLUE + "You have arrived at "
                        + ChatColor.WHITE + "[" + ChatColor.GREEN + w
                        + ChatColor.WHITE + "]");
                return;
            } else {
                WarpUtil.crossServerWarp(player.getUniqueId() + "", w,
                        targetServer);
                return;
            }
        }
        if (args.length == 2) {
            if (PlayerUtil.findPlayer(args[1]) == null) {
                player.sendMessage(ChatColor.RED + "Player not found.");
                return;
            }
            final Player tp = PlayerUtil.findPlayer(args[1]);
            final String w = args[0];
            Warp warp;
            if (WarpUtil.findWarp(w) == null) {
                player.sendMessage(ChatColor.RED + "Warp not found!");
                return;
            } else {
                warp = WarpUtil.findWarp(w);
            }
            final String targetServer = warp.getServer();
            String currentServer = MagicAssistant.serverName;
            final Location loc = warp.getLocation();
            if (targetServer.equals(currentServer)) {
                player.sendMessage(ChatColor.BLUE + tp.getName()
                        + " has arrived at " + ChatColor.WHITE + "["
                        + ChatColor.GREEN + w + ChatColor.WHITE + "]");
                if (tp.isInsideVehicle()) {
                    tp.getVehicle().eject();
                    Bukkit.getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugin("MagicAssistant"), new Runnable() {
                        @Override
                        public void run() {
                            tp.teleport(loc);
                            tp.sendMessage(ChatColor.BLUE + "You have arrived at "
                                    + ChatColor.WHITE + "[" + ChatColor.GREEN + w
                                    + ChatColor.WHITE + "]");
                        }
                    }, 10L);
                    return;
                }
                tp.teleport(warp.getLocation());
                tp.sendMessage(ChatColor.BLUE + "You have arrived at "
                        + ChatColor.WHITE + "[" + ChatColor.GREEN + w
                        + ChatColor.WHITE + "]");
                return;
            } else {
                WarpUtil.crossServerWarp(tp.getUniqueId() + "", w, targetServer);
                player.sendMessage(ChatColor.BLUE + tp.getName()
                        + " has arrived at " + ChatColor.WHITE + "["
                        + ChatColor.GREEN + w + ChatColor.WHITE + "]");
            }
        }
        listWarps(player);
    }


    public static void listWarps(Player player) {
        List<Warp> warps = MagicAssistant.warps;
        StringBuilder sb = new StringBuilder(ChatColor.GRAY + "");
        for (int i = 0; i < warps.size(); i++) {
            if (i == (warps.size() - 1)) {
                sb.append(warps.get(i).getName());
                continue;
            }
            sb.append(warps.get(i).getName()).append(", ");
        }
        player.sendMessage(ChatColor.GRAY + "Warps:");
        player.sendMessage(sb.toString());
    }
}