package network.palace.parkmanager.commands;

import network.palace.core.Core;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.parkmanager.ParkManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Marc on 3/10/15
 */
@CommandMeta(description = "Teleport to hub")
public class HubCommand extends CoreCommand {

    public HubCommand() {
        super("hub");
    }

    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        ParkManager parkManager = ParkManager.getInstance();
        if (!(sender instanceof Player)) {
            if (args.length > 0) {
                CPlayer tp = Core.getPlayerManager().getPlayer(args[0]);
                if (tp == null) {
                    sender.sendMessage(ChatColor.RED + "Player not found!");
                    return;
                }
                parkManager.getQueueManager().leaveAllQueues(tp);
                if (parkManager.getShooter() != null) {
                    parkManager.getShooter().warp(tp);
                }
                if (parkManager.getToyStoryMania() != null) {
                    parkManager.getToyStoryMania().done(tp);
                    ParkManager.getMuralUtil().done(tp);
                }
                if (tp.getBukkitPlayer().isInsideVehicle()) {
                    tp.sendMessage(ChatColor.RED + "You can't teleport while on a ride!");
                    return;
                }
                parkManager.getTeleportUtil().log(tp, tp.getLocation());
                tp.teleport(parkManager.getHub());
                tp.sendMessage(ChatColor.DARK_AQUA + "You have arrived at the Hub!");
            }
            return;
        }
        Player player = (Player) sender;
        CPlayer cp = Core.getPlayerManager().getPlayer(player);
        parkManager.getQueueManager().leaveAllQueues(cp);
        if (parkManager.getShooter() != null) {
            parkManager.getShooter().warp(cp);
        }
        if (parkManager.getToyStoryMania() != null) {
            parkManager.getToyStoryMania().done(cp);
            ParkManager.getMuralUtil().done(cp);
        }
        if (player.isInsideVehicle()) {
            player.sendMessage(ChatColor.RED + "You can't teleport while on a ride!");
            return;
        }
        parkManager.getTeleportUtil().log(player, player.getLocation());
        player.teleport(parkManager.getHub());
        sender.sendMessage(ChatColor.DARK_AQUA + "You have arrived at the Hub!");
    }
}
