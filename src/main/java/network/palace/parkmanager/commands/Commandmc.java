package network.palace.parkmanager.commands;

import network.palace.core.Core;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CommandPermission;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.Rank;
import network.palace.parkmanager.dashboard.packets.parks.PacketMuteChat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Marc on 2/19/16
 */
@CommandMeta(description = "Mute and unmute chat")
@CommandPermission(rank = Rank.KNIGHT)
public class Commandmc extends CoreCommand {

    public Commandmc() {
        super("mc");
    }

    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        if (Bukkit.getOnlinePlayers().isEmpty()) {
            sender.sendMessage(ChatColor.RED + "No players are online!");
            return;
        }
        String source = "";
        if (!(sender instanceof Player)) {
            source = "Server";
        } else {
            source = sender.getName();
        }
        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "/mc [mute/unmute]");
            return;
        }
        PacketMuteChat packet = new PacketMuteChat("ParkChat", args[0].equalsIgnoreCase("mute"), source);
        Core.getDashboardConnection().send(packet);
    }
}
