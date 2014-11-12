package com.legobuilder0813.MagicAssistant.Commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.legobuilder0813.MagicAssistant.MagicAssistant;

public class Command_delay {

	public static void execute(CommandSender sender, String label, String[] args) {
		if (sender instanceof Player) {
			sender.sendMessage(ChatColor.RED
					+ "Only command blocks can use this command!");
			return;
		}
		if (args.length != 4) {
			sender.sendMessage(ChatColor.RED + "Incorrect amount of arguments!");
		}
		if (MagicAssistant.isInt(args[0]) && MagicAssistant.isInt(args[1])
				&& MagicAssistant.isInt(args[2])
				&& MagicAssistant.isInt(args[3])) {
			int x = Integer.parseInt(args[1]);
			int y = Integer.parseInt(args[2]);
			int z = Integer.parseInt(args[3]);
			final Location loc = new Location(Bukkit.getWorlds().get(0), x, y,
					z);
			final Block b = loc.getBlock();
			Bukkit.getScheduler().scheduleSyncDelayedTask(
					Bukkit.getPluginManager().getPlugin("MagicAssistant"),
					new Runnable() {
						public void run() {
							b.setType(Material.REDSTONE_BLOCK);
							Bukkit.getScheduler().runTaskLater(
									Bukkit.getPluginManager().getPlugin(
											"MagicAssistant"), new Runnable() {
										public void run() {
											b.setType(Material.AIR);
										}
									}, 20L);
						}
					}, (20 * (Integer.parseInt(args[0]))));
			return;
		}
		sender.sendMessage(ChatColor.RED + "/delay [delay] x y z");
	}
}