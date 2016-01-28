package us.mcmagic.parkmanager.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.mcmagic.mcmagiccore.MCMagicCore;
import us.mcmagic.mcmagiccore.permissions.Rank;
import us.mcmagic.mcmagiccore.player.PlayerUtil;
import us.mcmagic.parkmanager.ParkManager;

public class Commandspawn implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            if (args.length == 1) {
                Player tp = PlayerUtil.findPlayer(args[0]);
                if (tp == null) {
                    sender.sendMessage(ChatColor.RED + "Player not found!");
                }
                ParkManager.teleportUtil.log(tp, tp.getLocation());
                tp.teleport(ParkManager.spawn);
                return true;
            }
            sender.sendMessage(ChatColor.RED + "/spawn [Username]");
            return true;
        }
        Player player = (Player) sender;
        if (MCMagicCore.getMCMagicConfig().serverName.equalsIgnoreCase("newhws") &&
                MCMagicCore.getUser(player.getUniqueId()).getRank().getRankId() < Rank.CASTMEMBER.getRankId()) {
            player.sendMessage(ChatColor.RED + "Sorry, you can't use this command in NewHWS!");
            return true;
        }
        if (args.length == 1) {
            if (player.hasPermission("spawn.other")) {
                Player tp = PlayerUtil.findPlayer(args[0]);
                if (tp == null) {
                    player.sendMessage(ChatColor.RED + "Player not found!");
                }
                ParkManager.queueManager.leaveAllQueues(tp);
                if (ParkManager.shooter != null) {
                    ParkManager.shooter.warp(tp);
                }
                ParkManager.teleportUtil.log(tp, tp.getLocation());
                tp.teleport(ParkManager.spawn);
                return true;
            }
        }
        ParkManager.queueManager.leaveAllQueues(player);
        if (ParkManager.shooter != null) {
            ParkManager.shooter.warp(player);
        }
        ParkManager.teleportUtil.log(player, player.getLocation());
        player.teleport(ParkManager.spawn);
        return true;
    }
}