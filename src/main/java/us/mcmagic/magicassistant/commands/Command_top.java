package us.mcmagic.magicassistant.commands;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Marc on 12/12/14
 */
public class Command_top {

    public static void execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this!");
            return;
        }
        Player player = (Player) sender;
        player.sendMessage(ChatColor.GRAY + "Teleporting to top.");
        player.teleport(getSafeLocation(player.getLocation()));
    }

    public static Location getSafeLocation(Location loc) {
        if ((loc == null) || (loc.getWorld() == null)) {
            return null;
        }
        World world = loc.getWorld();
        int x = loc.getBlockX();
        int y = (int) Math.round(loc.getY());
        int z = loc.getBlockZ();
        int origY = y;
        while (isBlockAboveAir(world, x, y, z)) {
            y--;
            if (y < 0) {
                y = origY;
            }
        }
        return new Location(world, x + 0.5D, y, z + 0.5D, loc.getYaw(), loc.getPitch());
    }

    static boolean isBlockAboveAir(World world, int x, int y, int z) {
        return y > world.getMaxHeight() || world.getBlockAt(x, y - 1, z).getType().equals(Material.AIR);
    }
}
