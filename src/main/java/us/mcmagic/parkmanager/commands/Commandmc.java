package us.mcmagic.parkmanager.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.mcmagic.parkmanager.ParkManager;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

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
        String senderName = "";
        if (!(sender instanceof Player)) {
            senderName = "Server";
        } else {
            senderName = sender.getName();
        }
        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "/mc [mute/unmute]");
            return true;
        }
        try {
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream d = new DataOutputStream(b);
            d.writeUTF("ParkChat" + (args[0].equalsIgnoreCase("mute") ? "Mute" : "Unmute"));
            d.writeUTF(senderName);
            if (sender instanceof Player) {
                ((Player) sender).sendPluginMessage(ParkManager.getInstance(), "BungeeCord", b.toByteArray());
            } else {
                ((Player) Bukkit.getOnlinePlayers().toArray()[0]).sendPluginMessage(ParkManager.getInstance(),
                        "BungeeCord", b.toByteArray());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        sender.sendMessage(ChatColor.RED + "/mc [mute/unmute]");
        return true;
    }
}