package us.mcmagic.magicassistant.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import us.mcmagic.magicassistant.MagicAssistant;
import us.mcmagic.magicassistant.handlers.Warp;
import us.mcmagic.magicassistant.utils.WarpUtil;

public class Command_delwarp implements CommandExecutor {

    @Override
    public boolean onCommand(final CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            final String w = args[0];
            final Warp warp = WarpUtil.findWarp(w);
            if (WarpUtil.findWarp(w) == null) {
                sender.sendMessage(ChatColor.RED + "Warp not found!");
                return true;
            }
            Bukkit.getScheduler().runTaskAsynchronously(MagicAssistant.getInstance(), new Runnable() {
                public void run() {
                    MagicAssistant.removeWarp(warp);
                    WarpUtil.removeWarp(warp);
                    WarpUtil.updateWarps();
                    sender.sendMessage(ChatColor.GRAY + "Warp " + w
                            + " has been removed.");
                }
            });
            return true;
        }
        sender.sendMessage(ChatColor.RED + "/" + label.toLowerCase() + " [Warp Name]");
        return true;
    }
}
