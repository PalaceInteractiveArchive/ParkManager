package us.mcmagic.parkmanager.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.mcmagic.parkmanager.ParkManager;
import us.mcmagic.mcmagiccore.player.PlayerUtil;

/**
 * Created by Marc on 3/10/15
 */
public class Commandhub implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            if (args.length > 0) {
                Player tp = PlayerUtil.findPlayer(args[0]);
                if (tp == null) {
                    sender.sendMessage(ChatColor.RED + "Player not found!");
                    return true;
                }
                ParkManager.teleportUtil.log(tp, tp.getLocation());
                tp.teleport(ParkManager.hub);
                tp.sendMessage(ChatColor.DARK_AQUA + "You have arrived at the Hub!");
            }
            return true;
        }
        Player player = (Player) sender;
        ParkManager.queueManager.leaveAllQueues(player);
        if (ParkManager.shooter != null) {
            ParkManager.shooter.warp(player);
        }
        ParkManager.teleportUtil.log(player, player.getLocation());
        player.teleport(ParkManager.hub);
        sender.sendMessage(ChatColor.DARK_AQUA + "You have arrived at the Hub!");
        return true;
    }
}
