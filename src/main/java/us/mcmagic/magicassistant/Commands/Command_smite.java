package us.mcmagic.magicassistant.Commands;

import us.mcmagic.magicassistant.Utils.PlayerUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Command_smite {

	@SuppressWarnings("deprecation")
	public static void execute(CommandSender sender, String label, String[] args) {
		if (!(sender instanceof Player)) {
			if (args.length == 1) {
				Player tp = PlayerUtil.findPlayer(args[0]);
				if (tp == null) {
					sender.sendMessage(ChatColor.RED + "Player not found!");
					return;
				}
				tp.getWorld().strikeLightning(tp.getLocation());
				tp.sendMessage(ChatColor.GRAY + "Thou hast been smitten!");
			}
			sender.sendMessage(ChatColor.RED + "/smite [Username]");
			return;
		}
		Player player = (Player) sender;
		if (args.length == 1) {
			Player tp = PlayerUtil.findPlayer(args[0]);
			if (tp == null) {
				player.sendMessage(ChatColor.RED + "Player not found!");
			}
			player.sendMessage(ChatColor.GRAY + "Smiting " + tp.getName());
			tp.getWorld().strikeLightning(tp.getLocation());
			tp.sendMessage(ChatColor.GRAY + "Thou hast been smitted!");
			return;
		}
		player.getWorld().strikeLightning(
				player.getTargetBlock(null, 600).getLocation());
		return;
	}
}