package us.mcmagic.parkmanager.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.mcmagic.parkmanager.ParkManager;
import us.mcmagic.parkmanager.handlers.Warp;
import us.mcmagic.parkmanager.utils.WarpUtil;
import us.mcmagic.mcmagiccore.MCMagicCore;

public class Commandsetwarp implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command!");
            return true;
        }
        final Player player = (Player) sender;
        if (args.length == 1) {
            final String w = args[0];
            Location loc = player.getLocation();
            if (WarpUtil.warpExists(w)) {
                player.sendMessage(ChatColor.RED
                        + "A warp already exists by that name! To change the location of that warp, type /uwarp [Warp Name]");
                return true;
            }
            final Warp warp = new Warp(w, MCMagicCore.getMCMagicConfig().serverName, loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(),
                    loc.getPitch(), loc.getWorld().getName());
            Bukkit.getScheduler().runTaskAsynchronously(ParkManager.getInstance(),
                    new Runnable() {
                        public void run() {
                            ParkManager.addWarp(warp);
                            WarpUtil.addWarp(warp);
                            WarpUtil.updateWarps();
                            player.sendMessage(ChatColor.GRAY + "Warp " + w + " set.");
                        }
                    });
            return true;
        }
        player.sendMessage(ChatColor.RED + "/setwarp [Warp Name]");
        return true;
    }
}