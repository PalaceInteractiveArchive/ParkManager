package us.mcmagic.magicassistant.commands;
import us.mcmagic.magicassistant.utils.PlayerUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Command_invsee {

	public static void execute(CommandSender sender, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED
					+ "Only players can use this command!");
			return;
		}
		Player player = (Player) sender;
		if (args.length == 1) {
			Player tp = PlayerUtil.findPlayer(args[0]);
			if (tp == null) {
				player.sendMessage(ChatColor.RED + "Player not found!");
				return;
			}
			player.sendMessage(ChatColor.GREEN + "Now looking in "
					+ tp.getName() + "'s Inventory!");
			player.openInventory(tp.getInventory());
			return;
		}
		player.sendMessage(ChatColor.RED + "/invsee [Username]");
		return;
	}
}