package us.mcmagic.magicassistant.Commands;

import us.mcmagic.magicassistant.Utils.PlayerUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Command_helpop {

	@SuppressWarnings("deprecation")
	public static void execute(CommandSender sender, String label, String[] args) {
		if (args.length > 0) {
			String message = "";
			for (int i = 0; i < args.length; i++) {
				message += args[i] + " ";
			}
			if (sender instanceof Player) {
				Player player = (Player) sender;
				for (Player tp : PlayerUtil.onlinePlayers()) {
					if (tp.hasPermission("arcade.cmchat")) {
						tp.sendMessage(ChatColor.DARK_RED
								+ "[CM CHAT] "
								+ ChatColor.GRAY
								+ player.getName()
								+ ": "
								+ ChatColor.WHITE
								+ ChatColor.translateAlternateColorCodes('&',
										message));
					}
				}
				return;
			}
			for (Player tp : PlayerUtil.onlinePlayers()) {
				if (tp.hasPermission("arcade.cmchat")) {
					tp.sendMessage(ChatColor.DARK_RED
							+ "[CM CHAT] "
							+ ChatColor.GRAY
							+ "Console"
							+ ": "
							+ ChatColor.WHITE
							+ ChatColor.translateAlternateColorCodes('&',
									message));
				}
			}
			return;
		}
		sender.sendMessage(ChatColor.RED + "/" + label + " [message]");
	}
}