package us.mcmagic.magicassistant.commands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class Command_more {

	public static void execute(CommandSender sender, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED
					+ "Only Players can use this command!");
			return;
		}
		Player player = (Player) sender;
		PlayerInventory pi = player.getInventory();
		if (pi.getItemInHand() == null
				|| pi.getItemInHand().getType().equals(Material.AIR)) {
			player.sendMessage(ChatColor.RED + "There is nothing in your hand!");
			return;
		}
		ItemStack stack = pi.getItemInHand();
		stack.setAmount(64);
	}
}