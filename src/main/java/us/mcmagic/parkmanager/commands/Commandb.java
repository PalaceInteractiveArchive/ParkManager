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
            try {
                ByteArrayOutputStream b = new ByteArrayOutputStream();
                DataOutputStream d = new DataOutputStream(b);
                d.writeUTF("ServerBroadcast");
                d.writeUTF(sender.getName());
                d.writeUTF(message);
                if (sender instanceof Player) {
                    ((Player) sender).sendPluginMessage(ParkManager.getInstance(), "BungeeCord", b.toByteArray());
                } else {
                    ((Player) Bukkit.getOnlinePlayers().toArray()[0]).sendPluginMessage(ParkManager.getInstance(),
                            "BungeeCord", b.toByteArray());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }
        sender.sendMessage(ChatColor.RED + "/b [Message]");
        return true;
    }
}