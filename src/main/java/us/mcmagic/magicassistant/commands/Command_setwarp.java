package us.mcmagic.magicassistant.commands;

import us.mcmagic.magicassistant.MagicAssistant;
import us.mcmagic.magicassistant.utils.WarpUtil;
import us.mcmagic.magicassistant.magicband.Warp;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Command_setwarp {

    public static void execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED
                    + "Only players can use this command!");
            return;
        }
        final Player player = (Player) sender;
        if (args.length == 1) {
            final String w = args[0];
            Location loc = player.getLocation();
            if (WarpUtil.warpExists(w)) {
                player.sendMessage(ChatColor.RED
                        + "A warp already exists by that name! To change the location of that warp, type /uwarp [Warp Name]");
                return;
            }
            final Warp warp = new Warp(w, MagicAssistant.serverName, loc.getX(),
                    loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch(), loc
                    .getWorld().getName());
            Bukkit.getScheduler().runTaskAsynchronously(WarpUtil.pl,
                    new Runnable() {
                        public void run() {
                            MagicAssistant.warps.add(warp);
                            WarpUtil.addWarp(warp);
                            WarpUtil.updateWarps();
                            player.sendMessage(ChatColor.GRAY + "Warp " + w
                                    + " set.");
                        }
                    });
            return;
        }
        player.sendMessage(ChatColor.RED + "/setwarp [Warp Name]");
    }
}