package us.mcmagic.magicassistant.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class Command_night {

    public static void execute(String label, String[] args, CommandSender sender) {
        Bukkit.getWorlds().get(0).setTime(16000);
        sender.sendMessage(ChatColor.GRAY + "Time has been set to "
                + ChatColor.GREEN + "Night.");
    }
}