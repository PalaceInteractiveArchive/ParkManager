package us.mcmagic.parkmanager.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.mcmagic.mcmagiccore.MCMagicCore;
import us.mcmagic.parkmanager.dashboard.packets.parks.PacketMuteChat;

/**
 * Created by Marc on 2/19/16
 */
public class Commandmc implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (Bukkit.getOnlinePlayers().isEmpty()) {
            sender.sendMessage(ChatColor.RED + "No players are online!");
            return true;
        }
        String source = "";
        if (!(sender instanceof Player)) {
            source = "Server";
        } else {
            source = sender.getName();
        }
        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "/mc [mute/unmute]");
            return true;
        }
        PacketMuteChat packet = new PacketMuteChat("ParkChat", args[0].equalsIgnoreCase("mute"), source);
        MCMagicCore.dashboardConnection.send(packet);
        return true;
    }
}