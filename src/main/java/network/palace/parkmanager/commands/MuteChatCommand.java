package network.palace.parkmanager.commands;

import network.palace.core.Core;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.Rank;
import network.palace.parkmanager.dashboard.packets.parks.PacketMuteChat;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandMeta(description = "Mute and unmute chat", rank = Rank.MOD)
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
            source = "Server";
        } else {
            source = sender.getName();
        }
        PacketMuteChat packet = new PacketMuteChat("ParkChat", args[0].equalsIgnoreCase("mute"), source);
        Core.getDashboardConnection().send(packet);
    }
}
