package com.legobuilder0813.MagicAssistant.Commands;

import com.legobuilder0813.MagicAssistant.MagicAssistant;
import com.legobuilder0813.MagicAssistant.Utils.WarpUtil;
import com.legobuilder0813.MagicAssistant.Warp;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class Command_delwarp {

	public static void execute(final CommandSender sender, String label,
			String[] args) {
		if (args.length == 1) {
			final String w = args[0];
			final Warp warp = WarpUtil.findWarp(w);
			if (WarpUtil.findWarp(w) == null) {
				sender.sendMessage(ChatColor.RED + "Warp not found!");
				return;
			}
			Bukkit.getScheduler().runTaskAsynchronously(WarpUtil.pl,
					new Runnable() {
						public void run() {
							MagicAssistant.warps.remove(warp);
							WarpUtil.removeWarp(warp);
							WarpUtil.updateWarps();
							sender.sendMessage(ChatColor.GRAY + "Warp " + w
									+ " has been removed.");
						}
					});
			return;
		}
		sender.sendMessage(ChatColor.RED + "/" + label.toLowerCase()
				+ " [Warp Name]");
		return;
	}
}
