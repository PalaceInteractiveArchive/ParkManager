package us.mcmagic.magicassistant.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.mcmagic.magicassistant.MagicAssistant;
import us.mcmagic.magicassistant.handlers.Warp;
import us.mcmagic.magicassistant.stitch.Stitch;
import us.mcmagic.magicassistant.utils.PlayerUtil;
import us.mcmagic.magicassistant.utils.WarpUtil;
import us.mcmagic.mcmagiccore.MCMagicCore;
import us.mcmagic.mcmagiccore.permissions.Rank;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Commandwarp implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            if (args.length == 2) {
                if (PlayerUtil.findPlayer(args[1]) == null) {
                    sender.sendMessage(ChatColor.RED + "Player not found.");
                    return true;
                }
                final Player tp = PlayerUtil.findPlayer(args[1]);
                final String w = args[0];
                Warp warp;
                if (WarpUtil.findWarp(w) == null) {
                    sender.sendMessage(ChatColor.RED + "Warp not found!");
                    return true;
                } else {
                    warp = WarpUtil.findWarp(w);
                }
                String targetServer = warp.getServer();
                String currentServer = MagicAssistant.serverName;
                final Location loc = warp.getLocation();
                if (targetServer.equals(currentServer)) {
                    if (tp.isInsideVehicle()) {
                        tp.getVehicle().eject();
                        Bukkit.getScheduler().runTaskLater(MagicAssistant.getInstance(), new Runnable() {
                            @Override
                            public void run() {
                                tp.teleport(loc);
                                tp.sendMessage(ChatColor.BLUE + "You have arrived at "
                                        + ChatColor.WHITE + "[" + ChatColor.GREEN + w
                                        + ChatColor.WHITE + "]");
                            }
                        }, 10L);
                        return true;
                    }
                    tp.teleport(warp.getLocation());
                    tp.sendMessage(ChatColor.BLUE + "You have arrived at "
                            + ChatColor.WHITE + "[" + ChatColor.GREEN + w
                            + ChatColor.WHITE + "]");
                    sender.sendMessage(ChatColor.BLUE + tp.getName()
                            + " has arrived at " + ChatColor.WHITE + "["
                            + ChatColor.GREEN + w + ChatColor.WHITE + "]");
                    return true;
                } else {
                    WarpUtil.crossServerWarp(tp.getUniqueId().toString(), w,
                            targetServer);
                    return true;
                }
            }
            sender.sendMessage(ChatColor.RED + "/warp [Warp Name] [Username]");
            return true;
        }
        final Player player = (Player) sender;
        if (args.length == 1) {
            final String w = args[0];
            Warp warp;
            if (WarpUtil.findWarp(w) == null) {
                player.sendMessage(ChatColor.RED + "Warp not found!");
                return true;
            } else {
                warp = WarpUtil.findWarp(w);
            }
            Rank rank = MCMagicCore.getUser(player.getUniqueId()).getRank();
            if (warp.getName().toLowerCase().startsWith("dvc")) {
                if (rank.getRankId() < Rank.DVCMEMBER.getRankId()) {
                    player.sendMessage(ChatColor.RED + "You must be the " + Rank.DVCMEMBER.getNameWithBrackets()
                            + ChatColor.RED + " rank or above to use this warp!");
                    return true;
                }
            }
            if (warp.getName().toLowerCase().startsWith("char")) {
                if (rank.getRankId() < Rank.CHARACTERGUEST.getRankId()) {
                    player.sendMessage(ChatColor.RED + "You must be the " + Rank.CHARACTERGUEST.getNameWithBrackets()
                            + ChatColor.RED + " rank or above to use this warp!");
                    return true;
                }
            }
            if (warp.getName().toLowerCase().startsWith("staff")) {
                if (rank.getRankId() < Rank.INTERN.getRankId()) {
                    player.sendMessage(ChatColor.RED + "You must be the " + Rank.INTERN.getNameWithBrackets()
                            + ChatColor.RED + " rank or above to use this warp!");
                    return true;
                }
            }
            String targetServer = warp.getServer();
            String currentServer = MagicAssistant.serverName;
            final Location loc = warp.getLocation();
            if (targetServer.equals(currentServer)) {
                if (player.isInsideVehicle()) {
                    player.getVehicle().eject();
                    Bukkit.getScheduler().runTaskLater(MagicAssistant.getInstance(), new Runnable() {
                        @Override
                        public void run() {
                            player.teleport(loc);
                            player.sendMessage(ChatColor.BLUE + "You have arrived at "
                                    + ChatColor.WHITE + "[" + ChatColor.GREEN + w
                                    + ChatColor.WHITE + "]");
                        }
                    }, 10L);
                    return true;
                }
                player.teleport(warp.getLocation());
                if (warp.getName().equalsIgnoreCase("sge")) {
                    Stitch stitch = MagicAssistant.stitch;
                    if (stitch.isWatching(player.getUniqueId())) {
                        stitch.leaveShow(player);
                    }
                } else {
                    player.sendMessage(ChatColor.BLUE + "You have arrived at "
                            + ChatColor.WHITE + "[" + ChatColor.GREEN + w
                            + ChatColor.WHITE + "]");
                }
                return true;
            } else {
                WarpUtil.crossServerWarp(player.getUniqueId().toString(), warp.getName(), targetServer);
                return true;
            }
        }
        if (args.length == 2) {
            Rank rank = MCMagicCore.getUser(player.getUniqueId()).getRank();
            if (rank.getRankId() < Rank.CASTMEMBER.getRankId()) {
            }
            if (PlayerUtil.findPlayer(args[1]) == null) {
                player.sendMessage(ChatColor.RED + "Player not found.");
                return true;
            }
            final Player tp = PlayerUtil.findPlayer(args[1]);
            final String w = args[0];
            Warp warp;
            if (WarpUtil.findWarp(w) == null) {
                player.sendMessage(ChatColor.RED + "Warp not found!");
                return true;
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
                    return true;
                }
                tp.teleport(warp.getLocation());
                tp.sendMessage(ChatColor.BLUE + "You have arrived at "
                        + ChatColor.WHITE + "[" + ChatColor.GREEN + w
                        + ChatColor.WHITE + "]");
                return true;
            } else {
                WarpUtil.crossServerWarp(tp.getUniqueId().toString(), w, targetServer);
                player.sendMessage(ChatColor.BLUE + tp.getName()
                        + " has arrived at " + ChatColor.WHITE + "["
                        + ChatColor.GREEN + w + ChatColor.WHITE + "]");
                return true;
            }
        }
        listWarps(player);
        return true;
    }


    public static void listWarps(Player player) {
        List<Warp> warps = MagicAssistant.getWarps();
        List<String> names = new ArrayList<>();
        for (Warp w : warps) {
            names.add(w.getName());
        }
        Collections.sort(names);
        StringBuilder sb = new StringBuilder(ChatColor.GRAY + "");
        for (int i = 0; i < names.size(); i++) {
            if (i == (names.size() - 1)) {
                sb.append(names.get(i));
                continue;
            }
            sb.append(names.get(i)).append(", ");
        }
        player.sendMessage(ChatColor.GRAY + "Warps:");
        player.sendMessage(sb.toString());
    }
}