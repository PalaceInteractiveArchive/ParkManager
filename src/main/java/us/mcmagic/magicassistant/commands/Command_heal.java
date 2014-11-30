package us.mcmagic.magicassistant.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import us.mcmagic.magicassistant.utils.PlayerUtil;

public class Command_heal {

	@SuppressWarnings("deprecation")
	public static void execute(CommandSender sender, String label, String[] args) {
		if (!(sender instanceof Player)) {
			if (args.length == 1) {
				if (args[0].equals("**")) {
					for (Player tp : PlayerUtil.onlinePlayers()) {
						healPlayer(tp);
						tp.sendMessage(ChatColor.GRAY + "You have been healed.");
						return;
					}
					return;
				}
				Player tp = PlayerUtil.findPlayer(args[0]);
				if (tp == null) {
					sender.sendMessage(ChatColor.RED + "Player not found!");
					return;
				}
				healPlayer(tp);
				tp.sendMessage(ChatColor.GRAY + "You have been healed.");
			}
			return;
		}
		Player player = (Player) sender;
		if (args.length == 1) {
			if (args[0].equalsIgnoreCase("**")) {
				for (Player tp : PlayerUtil.onlinePlayers()) {
					healPlayer(tp);
					tp.sendMessage(ChatColor.GRAY + "You have been healed.");
					return;
				}
				return;
			}
			Player tp = PlayerUtil.findPlayer(args[0]);
			if (tp == null) {
				player.sendMessage(ChatColor.RED + "Player not found!");
				return;
			}
			healPlayer(tp);
			player.sendMessage(ChatColor.GRAY + "You healed " + tp.getName());
			tp.sendMessage(ChatColor.GRAY + "You have been healed.");
			return;
		}
		healPlayer(player);
		player.sendMessage(ChatColor.GRAY + "You have been healed.");
	}

	public static void healPlayer(Player player) {
		player.setHealth(player.getHealthScale());
		player.setFoodLevel(20);
		player.setFireTicks(0);
		for (PotionEffect effect : player.getActivePotionEffects()) {
			player.removePotionEffect(effect.getType());
		}
	}
}