package us.mcmagic.magicassistant.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import us.mcmagic.magicassistant.MagicAssistant;
import us.mcmagic.magicassistant.utils.WarpUtil;

public class Commandwrl implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage(ChatColor.BLUE + "Reloading Warps...");
        MagicAssistant.clearWarps();
        WarpUtil.refreshWarps();
        sender.sendMessage(ChatColor.BLUE + "Warps Reloaded!");
        return true;
    }
}