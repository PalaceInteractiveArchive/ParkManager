package us.mcmagic.parkmanager.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.mcmagic.parkmanager.ParkManager;

public class Commanddelay implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            sender.sendMessage(ChatColor.RED + "/delay [delay] x y z");
            return true;
        }
        if (args.length != 4) {
            sender.sendMessage(ChatColor.RED + "Incorrect amount of arguments!");
            return true;
        }
        if (isDouble(args[0]) && isDouble(args[1]) && isDouble(args[2]) && isDouble(args[3])) {
            double x = Double.parseDouble(args[1]);
            double y = Double.parseDouble(args[2]);
            double z = Double.parseDouble(args[3]);
            final Location loc = new Location(Bukkit.getWorlds().get(0), x, y, z);
            if (!loc.getChunk().isLoaded()) {
                loc.getChunk().load();
            }
            final Block b = loc.getBlock();
            long delay = (long) (20 * (Double.parseDouble(args[0])));
            Bukkit.getScheduler().runTaskLater(ParkManager.getInstance(), () -> {
                b.setType(Material.REDSTONE_BLOCK);
                Bukkit.getScheduler().runTaskLater(ParkManager.getInstance(), () -> b.setType(Material.AIR), 20L);
            }, delay);
            return true;
        }
        sender.sendMessage(ChatColor.RED + "/delay [delay] x y z");
        return true;
    }

    private static boolean isDouble(String s) {
        try {
            Double.parseDouble(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}