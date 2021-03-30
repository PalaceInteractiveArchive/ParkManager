package network.palace.parkmanager.commands;

import network.palace.core.Core;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.parkmanager.ParkManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

@CommandMeta(description = "Add a ride counter for a player", rank = Rank.CM)
public class AddRideCounterCommand extends CoreCommand {

    public AddRideCounterCommand() {
        super("rc");
    }

    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "/rc [username] [ride name]");
            return;
        }
        CPlayer tp = Core.getPlayerManager().getPlayer(args[0]);
        if (tp == null) {
            sender.sendMessage(ChatColor.RED + "Player not found!");
            return;
        }
        if (!tp.isInVehicle()) {
            sender.sendMessage(ChatColor.RED + "That player is not in a vehicle!");
            return;
        }
        StringBuilder rideName = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            rideName.append(args[i]);
            rideName.append(" ");
        }
        String finalRideName = rideName.toString().trim();
        Core.runTaskAsynchronously(ParkManager.getInstance(), () -> ParkManager.getRideCounterUtil().logNewRide(tp, finalRideName, sender));
    }
}
