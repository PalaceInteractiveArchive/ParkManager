package us.mcmagic.magicassistant.Commands;

import us.mcmagic.magicassistant.MagicAssistant;
import us.mcmagic.magicassistant.Utils.FileUtil;
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