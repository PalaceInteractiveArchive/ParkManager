package us.mcmagic.magicassistant.commands;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CommandBlock;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Marc on 7/18/15
 */
public class Commandbalfix implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        Player player = (Player) sender;
        Location loc1 = player.getLocation().clone().add(-100, -100, -100);
        Location loc2 = player.getLocation().clone().add(100, 100, 100);
        for (int x = loc1.getBlockX(); x <= loc2.getBlockX(); x++) {
            for (int y = loc1.getBlockY(); y <= loc2.getBlockY(); y++) {
                for (int z = loc1.getBlockZ(); z <= loc2.getBlockZ(); z++) {
                    Block b = loc1.getWorld().getBlockAt(new Location(loc1.getWorld(), x, y, z));
                    if (!b.getType().equals(Material.COMMAND)) {
                        continue;
                    }
                    CommandBlock cmd = (CommandBlock) b.getState();
                    if (correct(cmd.getCommand())) {
                        cmd.setCommand(cmd.getCommand().toLowerCase().replace("coins", "bal"));
                        cmd.update();
                    }
                }
            }
        }
        return true;
    }

    private boolean correct(String s) {
        return s.startsWith("coins add");
    }
}