package us.mcmagic.magicassistant.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.mcmagic.magicassistant.MagicAssistant;
import us.mcmagic.magicassistant.magicband.Warp;
import us.mcmagic.magicassistant.utils.PlayerUtil;
import us.mcmagic.magicassistant.utils.WarpUtil;
import us.mcmagic.mcmagiccore.permissions.Rank;

import java.util.List;

public class Command_warp {

    public static void execute(String label, CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            if (args.length == 2) {
                if (PlayerUtil.findPlayer(args[1]) == null) {
                    sender.sendMessage(ChatColor.RED + "Player not found.");
                    return;
                }
                Player tp = PlayerUtil.findPlayer(args[1]);
                String w = args[0];
                Warp warp;
                if (WarpUtil.findWarp(w) == null) {
                    sender.sendMessage(ChatColor.RED + "Warp not found!");
                    return;
                } else {
                    warp = WarpUtil.findWarp(w);
                }
                String targetServer = warp.getServer();
                String currentServer = MagicAssistant.serverName;
                if (targetServer.equals(currentServer)) {
                    if (tp.isInsideVehicle()) {
                        tp.getVehicle().eject();
                        tp.teleport(warp.getLocation());
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
            String w = args[0];
            Warp warp;
            if (WarpUtil.findWarp(w) == null) {
                player.sendMessage(ChatColor.RED + "Warp not found!");
                return;
            } else {
                warp = WarpUtil.findWarp(w);
            }
            Rank rank = us.mcmagic.mcmagiccore.player.PlayerUtil.getUser(player.getUniqueId()).getRank();
            if (warp.getName().toLowerCase().startsWith("dvc")) {
                if (rank.equals(Rank.GUEST)) {
                    player.sendMessage(ChatColor.RED + "You must be the " + Rank.DVCMEMBER.getNameWithBrackets() + ChatColor.RED + " rank or above to use this warp!");
                    return;
                }
            }
            if (warp.getName().toLowerCase().startsWith("char")) {
                if (!(rank.getOp() || rank.getSqlName().startsWith("character") || rank.equals(Rank.INTERN))) {
                    player.sendMessage(ChatColor.RED + "You must be the " + Rank.CHARACTERGUEST.getNameWithBrackets() + ChatColor.RED + " rank or above to use this warp!");
                    return;
                }
            }
            String targetServer = warp.getServer();
            String currentServer = MagicAssistant.serverName;
            if (targetServer.equals(currentServer)) {
                if (player.isInsideVehicle()) {
                    player.getVehicle().eject();
                    player.teleport(warp.getLocation());
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
            if (targetServer.equals(currentServer)) {
                if (tp.isInsideVehicle()) {
                    tp.getVehicle().eject();
                    tp.teleport(warp.getLocation());
                }
                tp.teleport(warp.getLocation());
                tp.sendMessage(ChatColor.BLUE + "You have arrived at "
                        + ChatColor.WHITE + "[" + ChatColor.GREEN + w
                        + ChatColor.WHITE + "]");
                player.sendMessage(ChatColor.BLUE + tp.getName()
                        + " has arrived at " + ChatColor.WHITE + "["
                        + ChatColor.GREEN + w + ChatColor.WHITE + "]");
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