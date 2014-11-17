package com.legobuilder0813.MagicAssistant.Commands;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class Command_day {

	public static void execute(String cmd, String[] args, CommandSender sender) {
		Bukkit.getWorlds().get(0).setTime(1000);
		sender.sendMessage(ChatColor.GRAY + "Time has been set to "
				+ ChatColor.GREEN + "Day.");
	}
}