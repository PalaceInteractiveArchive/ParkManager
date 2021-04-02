package network.palace.parkmanager.commands;

import network.palace.core.Core;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.Rank;
import network.palace.parkmanager.message.ChatMutePacket;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;

@CommandMeta(description = "Mute and unmute chat", rank = Rank.CM)
public class MuteChatCommand extends CoreCommand {

    public MuteChatCommand() {
        super("mc");
    }

    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "/mc [mute/unmute]");
            return;
        }
        String source;
        if (!(sender instanceof Player)) {
            if (sender instanceof BlockCommandSender) {
                Location loc = ((BlockCommandSender) sender).getBlock().getLocation();
                source = "Server (" + Core.getInstanceName() + ", CMDBLK @ " + loc.getWorld().getName().toLowerCase() + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ() + ")";
            } else {
                source = "Server (" + Core.getInstanceName() + ")";
            }
        } else {
            source = sender.getName();
        }
        try {
            Core.getMessageHandler().sendMessage(new ChatMutePacket("ParkChat", source, args[0].equalsIgnoreCase("mute")), Core.getMessageHandler().ALL_PROXIES);
        } catch (IOException e) {
            e.printStackTrace();
            sender.sendMessage(ChatColor.RED + "An error occurred while muting/unmuting chat, check console for details.");
        }
    }
}
