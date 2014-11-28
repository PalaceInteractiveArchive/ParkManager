package us.mcmagic.magicassistant.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class Command_bc {

	public static void execute(CommandSender sender, String label, String[] args) {
		if (args.length < 1) {
			sender.sendMessage(ChatColor.RED + "/" + label + " [Message]");
			return;
		}
		String message = "";
		for (String s : args) message += s + " ";
		Bukkit.broadcastMessage(ChatColor.WHITE + "[" + ChatColor.AQUA
				+ "Information" + ChatColor.WHITE + "] " + ChatColor.GREEN
				+ ChatColor.translateAlternateColorCodes('&', message));
	}
}