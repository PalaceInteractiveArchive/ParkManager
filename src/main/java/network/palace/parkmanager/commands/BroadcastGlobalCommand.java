package network.palace.parkmanager.commands;

import network.palace.core.Core;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.Rank;
import network.palace.parkmanager.message.BroadcastPacket;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;

@CommandMeta(description = "Broadcast to the network", rank = Rank.CM)
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
        String message = String.join(" ", args);
        String source;
        if (!(sender instanceof Player)) {
            if (sender instanceof BlockCommandSender) {
                Location loc = ((BlockCommandSender) sender).getBlock().getLocation();
                source = "" + Core.getInstanceName() + ", CMDBLK @ " + loc.getWorld().getName().toLowerCase() + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ();
            } else {
                source = "Console on " + Core.getInstanceName();
            }
        } else {
            source = sender.getName();
        }
        try {
            Core.getMessageHandler().sendMessage(new BroadcastPacket(source, ChatColor.translateAlternateColorCodes('&', message)), Core.getMessageHandler().ALL_PROXIES);
        } catch (IOException e) {
            e.printStackTrace();
            sender.sendMessage(ChatColor.RED + "An error occurred while sending that broadcast, check console for details.");
        }
    }
}
