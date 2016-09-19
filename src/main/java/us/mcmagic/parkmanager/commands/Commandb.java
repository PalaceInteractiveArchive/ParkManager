package us.mcmagic.parkmanager.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.mcmagic.mcmagiccore.MCMagicCore;
import us.mcmagic.parkmanager.dashboard.packets.parks.PacketBroadcast;

/**
 * Created by Marc on 2/19/16
 */
public class Commandb implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (Bukkit.getOnlinePlayers().isEmpty()) {
            sender.sendMessage(ChatColor.RED + "No players are online!");
            return true;
        }
        if (args.length > 0) {
            String message = "";
            for (String arg : args) {
                message += arg + " ";
            }
            String source = sender.getName();
            if (!(sender instanceof Player)) {
                source = "Console on " + MCMagicCore.getMCMagicConfig().instanceName;
            }
            PacketBroadcast packet = new PacketBroadcast(message, source);
            MCMagicCore.dashboardConnection.send(packet);
            return true;
        }
        sender.sendMessage(ChatColor.RED + "/b [Message]");
        return true;
    }
}