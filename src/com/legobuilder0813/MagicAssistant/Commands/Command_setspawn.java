package com.legobuilder0813.MagicAssistant.Commands;

import com.legobuilder0813.MagicAssistant.MagicAssistant;
import com.legobuilder0813.MagicAssistant.Utils.FileUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Command_setspawn {

	public static void execute(CommandSender sender, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED
					+ "Only Players can use this command!");
			return;
		}
		Player player = (Player) sender;
		FileUtil.setSpawn(player.getLocation());
		MagicAssistant.spawn = player.getLocation();
		player.sendMessage(ChatColor.GRAY + "Spawn Set!");
		return;
	}
}