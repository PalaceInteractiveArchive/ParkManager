package com.legobuilder0813.MagicAssistant.Commands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class Command_mb {

	public static void execute(CommandSender sender, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED
					+ "Only In-Game Players can use this Command!");
			return;
		}
		Player player = (Player) sender;
		PlayerInventory pi = player.getInventory();
		ItemStack mb = new ItemStack(Material.PAPER);
		ItemMeta mbm = mb.getItemMeta();
		mbm.setDisplayName(ChatColor.GOLD + "MagicBand");
		mbm.setLore(Arrays.asList(ChatColor.GREEN + "Click me to open",
				ChatColor.GREEN + "the MagicBand menu!"));
		mb.setItemMeta(mbm);
		if (pi.contains(mb)) {
			pi.remove(mb);
		}
		pi.setItem(8, mb);
		player.sendMessage(ChatColor.GRAY + "MagicBand has been restored!");
	}
}