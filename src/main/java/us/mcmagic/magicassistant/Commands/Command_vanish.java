package us.mcmagic.magicassistant.commands;

import us.mcmagic.magicassistant.utils.PlayerUtil;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;

public class Command_vanish {
	public static HashMap<Player, ArrayList<Block>> hidden = new HashMap<Player, ArrayList<Block>>();

	@SuppressWarnings("deprecation")
	public static void execute(CommandSender sender, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Only players can use this!");
			return;
		}
		Player player = (Player) sender;
		if (args.length == 0) {
			if (hidden.containsKey(player)) {
				hidden.remove(player);
				for (Player tp : PlayerUtil.onlinePlayers()) {
					tp.showPlayer(player);
				}
				player.sendMessage(ChatColor.DARK_AQUA
						+ "You have become visible.");
				for (Player tp : PlayerUtil.onlinePlayers()) {
					if (tp.hasPermission("vanish.standard")
							&& !tp.equals(player)) {
						tp.sendMessage(ChatColor.YELLOW + player.getName()
								+ " has become visible.");
					}
				}
			} else {
				hidden.put(player, null);
				for (Player tp : PlayerUtil.onlinePlayers()) {
					if (!tp.hasPermission("vanish.standard")) {
						tp.hidePlayer(player);
					}
				}
				player.sendMessage(ChatColor.DARK_AQUA
						+ "You have vanished. Poof.");
				for (Player tp : PlayerUtil.onlinePlayers()) {
					if (tp.hasPermission("vanish.standard")
							&& !tp.equals(player)) {
						tp.sendMessage(ChatColor.YELLOW + player.getName()
								+ " has vanished. Poof.");
					}
				}
			}
			return;
		}
		if (args.length == 1) {
			if (args[0].equalsIgnoreCase("list")) {
				StringBuilder list = new StringBuilder();
				for (Player tp : PlayerUtil.onlinePlayers()) {
					if (hidden.containsKey(tp)) {
						if (list.length() > 0) {
							list.append(ChatColor.DARK_AQUA);
							list.append(", ");
						}
						list.append(ChatColor.AQUA);
						list.append(tp.getName());
					}
				}
				list.insert(0, "Vanished: ");
				list.insert(0, ChatColor.DARK_AQUA);
				player.sendMessage(list.toString());
				return;
			}
			if (args[0].equalsIgnoreCase("check")) {
				if (hidden.containsKey(player)) {
					player.sendMessage(ChatColor.DARK_AQUA
							+ "You are vanished.");
				} else {
					player.sendMessage(ChatColor.DARK_AQUA
							+ "You are not vanished.");
				}
				return;
			}
		}
	}
}