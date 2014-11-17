package us.mcmagic.magicassistant.Commands;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class Command_noon {

	public static void execute(String label, String[] args, CommandSender sender) {
		Bukkit.getWorlds().get(0).setTime(6000);
		sender.sendMessage(ChatColor.GRAY + "Time has been set to "
				+ ChatColor.GREEN + "Noon.");
	}
}