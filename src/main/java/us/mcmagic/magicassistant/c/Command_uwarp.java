package us.mcmagic.magicassistant.commands;
import us.mcmagic.magicassistant.MagicAssistant;
import us.mcmagic.magicassistant.utils.WarpUtil;
import us.mcmagic.magicassistant.Warp;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Command_uwarp {

	public static void execute(CommandSender sender, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED
					+ "Only players can use this command!");
			return;
		}
		final Player player = (Player) sender;
		if (args.length == 1) {
			final String w = args[0];
			if (!WarpUtil.warpExists(w)) {
				player.sendMessage(ChatColor.RED
						+ "A warp doesn'commands exist by that name! To add a warp, type /setwarp [Warp Name]");
				return;
			}
			Location loc = player.getLocation();
			final Warp warp = WarpUtil.findWarp(w);
			final Warp newWarp = new Warp(w, MagicAssistant.serverName, loc.getX(),
					loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch(), loc
							.getWorld().getName());
			Bukkit.getScheduler().runTaskAsynchronously(WarpUtil.pl,
					new Runnable() {
						public void run() {
							MagicAssistant.warps.remove(warp);
							MagicAssistant.warps.add(newWarp);
							WarpUtil.removeWarp(warp);
							WarpUtil.addWarp(newWarp);
							WarpUtil.updateWarps();
							player.sendMessage(ChatColor.GRAY + "Warp " + w
									+ " has been updated.");
						}
					});
			return;
		}
		player.sendMessage(ChatColor.RED + "/uwarp [Warp Name]");
	}
}