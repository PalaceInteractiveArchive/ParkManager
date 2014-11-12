package com.legobuilder0813.MagicAssistant.Commands;

import com.legobuilder0813.MagicAssistant.MagicAssistant;
import com.legobuilder0813.MagicAssistant.Utils.PlayerUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Command_spawn {

	public static void execute(CommandSender sender, String label, String[] args) {
		if (!(sender instanceof Player)) {
			if (args.length == 1) {
				Player tp = PlayerUtil.findPlayer(args[0]);
				if (tp == null) {
					sender.sendMessage(ChatColor.RED + "Player not found!");
				}
				tp.teleport(MagicAssistant.spawn);
				return;
			}
			sender.sendMessage(ChatColor.RED + "/spawn [Username]");
			return;
		}
		Player player = (Player) sender;
		if (args.length == 1) {
			if (player.hasPermission("spawn.other")) {
				Player tp = PlayerUtil.findPlayer(args[0]);
				if (tp == null) {
					player.sendMessage(ChatColor.RED + "Player not found!");
				}
				tp.teleport(MagicAssistant.spawn);
			} else {
				player.teleport(MagicAssistant.spawn);
			}
			return;
		}
		player.teleport(MagicAssistant.spawn);
	}
}