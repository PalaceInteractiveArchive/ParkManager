package com.legobuilder0813.MagicAssistant.Commands;

import com.legobuilder0813.MagicAssistant.Utils.PlayerUtil;
import org.bukkit.ChatColor;
import org.bukkit.WeatherType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Command_pweather {

	public static void execute(CommandSender sender, String label, String[] args) {
		if (!(sender instanceof Player)) {
			if (args.length == 2) {
				Player tp = PlayerUtil.findPlayer(args[1]);
				if (tp == null) {
					sender.sendMessage(ChatColor.RED + "Player not found!");
					return;
				}
				args[0] = args[0].toLowerCase();
				switch (args[0]) {
				case "sun":
					tp.setPlayerWeather(WeatherType.CLEAR);
					sender.sendMessage(ChatColor.DARK_AQUA + tp.getName()
							+ "'s " + ChatColor.GREEN
							+ "weather has been set to " + ChatColor.DARK_AQUA
							+ "Clear" + ChatColor.GREEN + "!");
					break;
				case "rain":
					tp.setPlayerWeather(WeatherType.DOWNFALL);
					sender.sendMessage(ChatColor.DARK_AQUA + tp.getName()
							+ "'s " + ChatColor.GREEN
							+ "weather has been set to " + ChatColor.DARK_AQUA
							+ "Storm" + ChatColor.GREEN + "!");
					break;
				case "reset":
					tp.resetPlayerWeather();
					sender.sendMessage(ChatColor.DARK_AQUA + tp.getName()
							+ "'s " + ChatColor.GREEN
							+ "weather now matches the server.");
				default:
					sender.sendMessage(ChatColor.RED
							+ "/pweather [rain/sun/reset] [Username]");
					break;
				}
			}
			sender.sendMessage(ChatColor.RED
					+ "/pweather [rain/sun/reset] [Username]");
			return;
		}
		Player player = (Player) sender;
		if (args.length == 1) {
			args[0] = args[0].toLowerCase();
			switch (args[0]) {
			case "sun":
				player.setPlayerWeather(WeatherType.CLEAR);
				player.sendMessage(ChatColor.GREEN
						+ "Your weather has been set to " + ChatColor.DARK_AQUA
						+ "Clear" + ChatColor.GREEN + "!");
				break;
			case "rain":
				player.setPlayerWeather(WeatherType.DOWNFALL);
				player.sendMessage(ChatColor.GREEN
						+ "Your weather has been set to " + ChatColor.DARK_AQUA
						+ "Storm" + ChatColor.GREEN + "!");
				break;
			case "reset":
				player.resetPlayerWeather();
				player.sendMessage(ChatColor.GREEN
						+ "Your weather now matches the server");
			default:
				player.sendMessage(ChatColor.RED
						+ "/pweather [rain/sun/reset] [Username]");
				break;
			}
			return;
		}
		if (args.length == 2) {
			Player tp = PlayerUtil.findPlayer(args[1]);
			if (tp == null) {
				player.sendMessage(ChatColor.RED + "Player not found!");
				return;
			}
			args[0] = args[0].toLowerCase();
			switch (args[0]) {
			case "sun":
				tp.setPlayerWeather(WeatherType.CLEAR);
				player.sendMessage(ChatColor.DARK_AQUA + tp.getName() + "'s "
						+ ChatColor.GREEN + "weather has been set to "
						+ ChatColor.DARK_AQUA + "Clear" + ChatColor.GREEN + "!");
				break;
			case "rain":
				tp.setPlayerWeather(WeatherType.DOWNFALL);
				player.sendMessage(ChatColor.DARK_AQUA + tp.getName() + "'s "
						+ ChatColor.GREEN + "weather has been set to "
						+ ChatColor.DARK_AQUA + "Storm" + ChatColor.GREEN + "!");
				break;
			case "reset":
				tp.resetPlayerWeather();
				player.sendMessage(ChatColor.DARK_AQUA + tp.getName() + "'s "
						+ ChatColor.GREEN + "weather now matches the server.");
			default:
				player.sendMessage(ChatColor.RED
						+ "/pweather [rain/sun/reset] [Username]");
				break;
			}
		}
		player.sendMessage(ChatColor.RED
				+ "/pweather [rain/sun/reset] [Username]");
	}
}