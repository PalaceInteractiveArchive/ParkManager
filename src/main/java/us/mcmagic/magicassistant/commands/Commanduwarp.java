package us.mcmagic.magicassistant.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.mcmagic.magicassistant.MagicAssistant;
import us.mcmagic.magicassistant.handlers.Warp;
import us.mcmagic.magicassistant.utils.WarpUtil;
import us.mcmagic.mcmagiccore.MCMagicCore;

public class Commanduwarp implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED
                    + "Only players can use this command!");
            return true;
        }
        final Player player = (Player) sender;
        if (args.length == 1) {
            final String w = args[0];
            if (!WarpUtil.warpExists(w)) {
                player.sendMessage(ChatColor.RED
                        + "A warp doesn't exist by that name! To add a warp, type /setwarp [Warp Name]");
                return true;
            }
            Location loc = player.getLocation();
            final Warp warp = WarpUtil.findWarp(w);
            final Warp newWarp = new Warp(w, MCMagicCore.getMCMagicConfig().serverName, loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(),
                    loc.getPitch(), loc.getWorld().getName());
            Bukkit.getScheduler().runTaskAsynchronously(MagicAssistant.getInstance(),
                    new Runnable() {
                        public void run() {
                            MagicAssistant.removeWarp(warp);
                            MagicAssistant.addWarp(newWarp);
                            WarpUtil.removeWarp(warp);
                            WarpUtil.addWarp(newWarp);
                            WarpUtil.updateWarps();
                            player.sendMessage(ChatColor.GRAY + "Warp " + w
                                    + " has been updated.");
                        }
                    });
            return true;
        }
        player.sendMessage(ChatColor.RED + "/uwarp [Warp Name]");
        return true;
    }
}