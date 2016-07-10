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
                if (tp.isInsideVehicle()) {
                    tp.sendMessage(ChatColor.RED + "You can't teleport while on a ride!");
                    return true;
                }
                ParkManager.teleportUtil.log(tp, tp.getLocation());
                tp.teleport(ParkManager.spawn);
                return true;
            }
            sender.sendMessage(ChatColor.RED + "/spawn [Username]");
            return true;
        }
        Player player = (Player) sender;
        if (args.length == 1) {
            if (MCMagicCore.getUser(player.getUniqueId()).getRank().getRankId() >= Rank.CASTMEMBER.getRankId()) {
                Player tp = PlayerUtil.findPlayer(args[0]);
                if (tp == null) {
                    player.sendMessage(ChatColor.RED + "Player not found!");
                }
                ParkManager.queueManager.leaveAllQueues(tp);
                if (ParkManager.shooter != null) {
                    ParkManager.shooter.warp(tp);
                }
                if (ParkManager.toyStoryMania != null) {
                    ParkManager.toyStoryMania.done(tp);
                }
                if (tp.isInsideVehicle()) {
                    tp.sendMessage(ChatColor.RED + "You can't teleport while on a ride!");
                    return true;
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
        if (ParkManager.toyStoryMania != null) {
            ParkManager.toyStoryMania.done(player);
        }
        if (player.isInsideVehicle()) {
            player.sendMessage(ChatColor.RED + "You can't teleport while on a ride!");
            return true;
        }
        ParkManager.teleportUtil.log(player, player.getLocation());
        player.teleport(ParkManager.spawn);
        return true;
    }
}