package us.mcmagic.parkmanager.commands;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Marc on 12/12/14
 */
public class Commandtop implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this!");
            return true;
        }
        Player player = (Player) sender;
        player.sendMessage(ChatColor.GRAY + "Teleporting to top.");
        player.teleport(getSafeLocation(player.getLocation()));
        return true;
    }

    public static Location getSafeLocation(Location loc) {
        if ((loc == null) || (loc.getWorld() == null)) {
            return null;
        }
        World world = loc.getWorld();
        int x = loc.getBlockX();
        int y = (int) Math.round(loc.getY());
        int z = loc.getBlockZ();
        final int origY = y;
        if (origY < 0) {
            return loc;
        }
        while (!isBlockAboveAir(world, x, y, z)) {
            y++;
            if (y < 0) {
                y = origY;
            }
        }
        return new Location(world, x + 0.5D, y + 1, z + 0.5D, loc.getYaw(), loc.getPitch());
    }

    static boolean isBlockAboveAir(World world, int x, int y, int z) {
        return y < 256 && world.getBlockAt(x, y + 1, z).getType().equals(Material.AIR);
    }
}
