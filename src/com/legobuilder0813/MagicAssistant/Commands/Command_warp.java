package com.legobuilder0813.MagicAssistant.Commands;

import java.io.File;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.legobuilder0813.MagicAssistant.MagicAssistant;
import com.legobuilder0813.MagicAssistant.Utils.WarpUtil;

public class Command_warp {

	public static void execute(String label, CommandSender sender, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED
					+ "Only players can use this command!");
			return;
		}
		Player player = (Player) sender;
		if (args.length == 0 || args.length > 2) {
			player.sendMessage(ChatColor.RED + "/warp [Warp Name]");
			return;
		}
		if (args.length == 1) {
			File cfile = new File("plugins/MagicAssistant/config.yml");
			YamlConfiguration config = YamlConfiguration
					.loadConfiguration(cfile);
			String warp = args[0];
			String targetServer = WarpUtil.getServer(warp);
			String currentServer = config.getString("server-name");
			if (targetServer.equals(currentServer)) {
				player.teleport(WarpUtil.getLocation(warp));
				player.sendMessage(ChatColor.BLUE + "You have arrived at "
						+ ChatColor.WHITE + "[" + ChatColor.GREEN + warp
						+ ChatColor.WHITE + "]");
				return;
			} else {
				WarpUtil.setWarpForPlayer(player.getUniqueId() + "", warp);
				MagicAssistant.sendToServer(player, targetServer);
			}
		}
	}
}