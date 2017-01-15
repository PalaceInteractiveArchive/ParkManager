package network.palace.parkmanager.commands;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import network.palace.parkmanager.ParkManager;

/**
 * Created by Marc on 3/10/15
 */
@CommandMeta(description = "Teleport to hub")
public class Commandhub extends CoreCommand {

    public Commandhub() {
        super("hub");
    }

    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        if (!(sender instanceof Player)) {
            if (args.length > 0) {
                Player tp = Bukkit.getPlayer(args[0]);
                if (tp == null) {
                    sender.sendMessage(ChatColor.RED + "Player not found!");
                    return;
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
                    return;
                }
                ParkManager.teleportUtil.log(tp, tp.getLocation());
                tp.teleport(ParkManager.hub);
                tp.sendMessage(ChatColor.DARK_AQUA + "You have arrived at the Hub!");
            }
            return;
        }
        Player player = (Player) sender;
        ParkManager.queueManager.leaveAllQueues(player);
        if (ParkManager.shooter != null) {
            ParkManager.shooter.warp(player);
        }
        if (ParkManager.toyStoryMania != null) {
            ParkManager.toyStoryMania.done(player);
        }
        if (player.isInsideVehicle()) {
            player.sendMessage(ChatColor.RED + "You can't teleport while on a ride!");
            return;
        }
        ParkManager.teleportUtil.log(player, player.getLocation());
        player.teleport(ParkManager.hub);
        sender.sendMessage(ChatColor.DARK_AQUA + "You have arrived at the Hub!");
    }
}
