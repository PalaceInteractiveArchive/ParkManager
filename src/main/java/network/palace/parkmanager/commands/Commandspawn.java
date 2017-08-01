package network.palace.parkmanager.commands;

import network.palace.core.Core;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.Rank;
import network.palace.parkmanager.ParkManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandMeta(description = "Teleport to spawn")
public class Commandspawn extends CoreCommand {

    public Commandspawn() {
        super("spawn");
    }

    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        ParkManager parkManager = ParkManager.getInstance();
        if (!(sender instanceof Player)) {
            if (args.length == 1) {
                Player tp = Bukkit.getPlayer(args[0]);
                if (tp == null) {
                    sender.sendMessage(ChatColor.RED + "Player not found!");
                }
                if (tp.isInsideVehicle()) {
                    tp.sendMessage(ChatColor.RED + "You can't teleport while on a ride!");
                    return;
                }
                parkManager.getTeleportUtil().log(tp, tp.getLocation());
                tp.teleport(parkManager.getSpawn());
                return;
            }
            sender.sendMessage(ChatColor.RED + "/spawn [Username]");
            return;
        }
        Player player = (Player) sender;
        if (args.length == 1) {
            if (Core.getPlayerManager().getPlayer(player.getUniqueId()).getRank().getRankId() >= Rank.KNIGHT.getRankId()) {
                Player tp = Bukkit.getPlayer(args[0]);
                if (tp == null) {
                    player.sendMessage(ChatColor.RED + "Player not found!");
                }
                parkManager.getQueueManager().leaveAllQueues(tp);
                if (parkManager.getShooter() != null) {
                    parkManager.getShooter().warp(tp);
                }
                if (parkManager.getToyStoryMania() != null) {
                    parkManager.getToyStoryMania().done(tp);
                }
                if (tp.isInsideVehicle()) {
                    tp.sendMessage(ChatColor.RED + "You can't teleport while on a ride!");
                    return;
                }
                parkManager.getTeleportUtil().log(tp, tp.getLocation());
                tp.teleport(parkManager.getSpawn());
                return;
            }
        }
        parkManager.getQueueManager().leaveAllQueues(player);
        if (parkManager.getShooter() != null) {
            parkManager.getShooter().warp(player);
        }
        if (parkManager.getToyStoryMania() != null) {
            parkManager.getToyStoryMania().done(player);
        }
        if (player.isInsideVehicle()) {
            player.sendMessage(ChatColor.RED + "You can't teleport while on a ride!");
            return;
        }
        parkManager.getTeleportUtil().log(player, player.getLocation());
        player.teleport(parkManager.getSpawn());
    }
}
