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
import us.mcmagic.magicassistant.utils.WarpUtil;
import us.mcmagic.mcmagiccore.MCMagicCore;
import us.mcmagic.mcmagiccore.chat.formattedmessage.FormattedMessage;
import us.mcmagic.mcmagiccore.permissions.Rank;
import us.mcmagic.mcmagiccore.player.PlayerUtil;

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
                String currentServer = MCMagicCore.getMCMagicConfig().serverName;
                final Location loc = warp.getLocation();
                if (targetServer.equals(currentServer)) {
                    MagicAssistant.queueManager.leaveAllQueues(tp);
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
                    WarpUtil.crossServerWarp(tp.getUniqueId(), w,
                            targetServer);
                    return true;
                }
            }
            sender.sendMessage(ChatColor.RED + "/warp [Warp Name] [Username]");
            return true;
        }
        final Player player = (Player) sender;
        if (args.length == 1) {
            if (isInt(args[0])) {
                listWarps(player, Integer.parseInt(args[0]));
                return true;
            }
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
            String currentServer = MCMagicCore.getMCMagicConfig().serverName;
            final Location loc = warp.getLocation();
            if (targetServer.equals(currentServer)) {
                MagicAssistant.queueManager.leaveAllQueues(player);
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
                    if (MagicAssistant.stitch.isWatching(player.getUniqueId())) {
                        MagicAssistant.stitch.leaveShow(player);
                    }
                } else {
                    player.sendMessage(ChatColor.BLUE + "You have arrived at "
                            + ChatColor.WHITE + "[" + ChatColor.GREEN + w
                            + ChatColor.WHITE + "]");
                }
                return true;
            } else {
                WarpUtil.crossServerWarp(player.getUniqueId(), warp.getName(), targetServer);
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
            String currentServer = MCMagicCore.getMCMagicConfig().serverName;
            final Location loc = warp.getLocation();
            if (targetServer.equals(currentServer)) {
                MagicAssistant.queueManager.leaveAllQueues(tp);
                player.sendMessage(ChatColor.BLUE + tp.getName()
                        + " has arrived at " + ChatColor.WHITE + "["
                        + ChatColor.GREEN + w + ChatColor.WHITE + "]");
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
                return true;
            } else {
                WarpUtil.crossServerWarp(tp.getUniqueId(), w, targetServer);
                player.sendMessage(ChatColor.BLUE + tp.getName()
                        + " has arrived at " + ChatColor.WHITE + "["
                        + ChatColor.GREEN + w + ChatColor.WHITE + "]");
                return true;
            }
        }
        listWarps(player, 1);
        return true;
    }

    private boolean isInt(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }


    public static void listWarps(Player player, int page) {
        List<Warp> warps = MagicAssistant.getWarps();
        List<String> nlist = new ArrayList<>();
        for (Warp w : warps) {
            nlist.add(w.getName());
        }
        Collections.sort(nlist);
        if (nlist.size() < (page - 1) * 20) {
            listWarps(player, 1);
            return;
        }
        int max = page * 20;
        List<String> names = nlist.subList(20 * (page - 1), nlist.size() < max ? nlist.size() : max);
        FormattedMessage msg = new FormattedMessage("Warps (Page " + page + "):\n").color(ChatColor.GRAY);
        for (int i = 0; i < names.size(); i++) {
            String warp = names.get(i);
            if (i == (names.size() - 1)) {
                msg.then(warp).color(ChatColor.GRAY).command("/warp " + warp).tooltip(ChatColor.GREEN +
                        "Click to warp to " + ChatColor.BLUE + warp + ChatColor.GREEN + "!");
                continue;
            }
            msg.then(warp + ", ").color(ChatColor.GRAY).command("/warp " + warp).tooltip(ChatColor.GREEN +
                    "Click to warp to " + ChatColor.BLUE + warp + ChatColor.GREEN + "!");
        }
        msg.send(player);
    }
}