package network.palace.parkmanager.commands;

import network.palace.core.Core;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.Rank;
import network.palace.parkmanager.dashboard.packets.parks.PacketBroadcast;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Marc on 2/19/16
 */
@CommandMeta(description = "Broadcast to the network", rank = Rank.MOD)
public class BroadcastGlobalCommand extends CoreCommand {

    public BroadcastGlobalCommand() {
        super("b");
    }

    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        if (Bukkit.getOnlinePlayers().isEmpty()) {
            sender.sendMessage(ChatColor.RED + "No players are online!");
            return;
        }
        if (args.length > 0) {
            StringBuilder message = new StringBuilder();
            for (String arg : args) {
                message.append(arg).append(" ");
            }
            String source = sender.getName();
            if (!(sender instanceof Player)) {
                source = "Console on " + Core.getInstanceName();
            }
            PacketBroadcast packet = new PacketBroadcast(message.toString(), source);
            Core.getDashboardConnection().send(packet);
            return;
        }
        sender.sendMessage(ChatColor.RED + "/b [Message]");
    }
}
