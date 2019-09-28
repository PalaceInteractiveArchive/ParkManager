package network.palace.parkmanager.commands;

import network.palace.core.Core;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.Rank;
import network.palace.parkmanager.dashboard.packets.parks.PacketBroadcast;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandMeta(description = "Broadcast to the network", rank = Rank.MOD)
public class BroadcastGlobalCommand extends CoreCommand {

    public BroadcastGlobalCommand() {
        super("b");
    }

    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "/b [Message]");
            return;
        }
        StringBuilder message = new StringBuilder();
        for (String arg : args) {
            message.append(arg).append(" ");
        }
        String source;
        if (!(sender instanceof Player)) {
            source = "Console on " + Core.getInstanceName();
        } else {
            source = sender.getName();
        }
        PacketBroadcast packet = new PacketBroadcast(message.toString(), source);
        Core.getDashboardConnection().send(packet);
    }
}
