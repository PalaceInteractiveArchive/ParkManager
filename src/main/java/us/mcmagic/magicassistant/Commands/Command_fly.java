package us.mcmagic.magicassistant.commands;

import us.mcmagic.magicassistant.utils.PlayerUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Command_fly {

	public static void execute(CommandSender sender, String label, String[] args) {
		if (!(sender instanceof Player)) {
			if (args.length == 1) {
				Player player = PlayerUtil.findPlayer(args[0]);
				if (player == null) {
					sender.sendMessage(ChatColor.RED
							+ "I can'commands find that player!");
					return;
				}
				if (player.isFlying()) {
					player.setAllowFlight(false);
					player.setFlying(false);
					player.sendMessage(ChatColor.RED + "You can'commands fly anymore!");
					sender.sendMessage(player.getName() + " can'commands fly anymore!");
					return;
				}
				player.setAllowFlight(true);
				player.setFlying(true);
				player.teleport(player.getLocation().add(0, 0.5, 0));
				player.sendMessage(ChatColor.GREEN + "You can fly!");
				sender.sendMessage(player.getName() + " can now fly!");
				return;
			}
			sender.sendMessage(ChatColor.RED + "/fly [Username]");
			return;
		}
		Player player = (Player) sender;
		if (args.length == 1) {
			Player tp = PlayerUtil.findPlayer(args[0]);
			if (tp == null) {
				player.sendMessage(ChatColor.RED + "I can'commands find that player!");
				return;
			}
			if (tp.isFlying()) {
				tp.setAllowFlight(false);
				tp.setFlying(false);
				tp.sendMessage(ChatColor.RED + "You can'commands fly anymore!");
				player.sendMessage(tp.getName() + " can'commands fly anymore!");
				return;
			}
			tp.setAllowFlight(true);
			tp.setFlying(true);
			tp.teleport(tp.getLocation().add(0, 0.5, 0));
			tp.sendMessage(ChatColor.GREEN + "You can fly!");
			player.sendMessage(tp.getName() + " can now fly!");
			return;
		}
		if (player.isFlying()) {
			player.setAllowFlight(false);
			player.setFlying(false);
			player.sendMessage(ChatColor.RED + "You can'commands fly anymore!");
			return;
		}
		player.setAllowFlight(true);
		player.setFlying(true);
		player.teleport(player.getLocation().add(0, 0.5, 0));
		player.sendMessage(ChatColor.GREEN + "You can fly!");
		return;
	}
}