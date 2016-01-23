package us.mcmagic.parkmanager.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import us.mcmagic.parkmanager.ParkManager;
import us.mcmagic.parkmanager.handlers.Warp;
import us.mcmagic.parkmanager.utils.WarpUtil;

public class Commanddelwarp implements CommandExecutor {

    @Override
    public boolean onCommand(final CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            final String w = args[0];
            final Warp warp = WarpUtil.findWarp(w);
            if (WarpUtil.findWarp(w) == null) {
                sender.sendMessage(ChatColor.RED + "Warp not found!");
                return true;
            }
            Bukkit.getScheduler().runTaskAsynchronously(ParkManager.getInstance(), new Runnable() {
                public void run() {
                    ParkManager.removeWarp(warp);
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
