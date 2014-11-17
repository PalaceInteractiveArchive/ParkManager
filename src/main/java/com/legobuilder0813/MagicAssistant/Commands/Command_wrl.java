package com.legobuilder0813.MagicAssistant.Commands;
import com.legobuilder0813.MagicAssistant.MagicAssistant;
import com.legobuilder0813.MagicAssistant.Utils.WarpUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class Command_wrl {

	public static void execute(CommandSender sender, String label, String[] args) {
		sender.sendMessage(ChatColor.BLUE + "Reloading Warps...");
		MagicAssistant.warps.clear();
		WarpUtil.refreshWarps();
		sender.sendMessage(ChatColor.BLUE + "Warps Reloaded!");
	}
}