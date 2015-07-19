package us.mcmagic.magicassistant.commands;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.mcmagic.magicassistant.MagicAssistant;

/**
 * Created by Marc on 7/18/15
 */
public class Commandsignfix implements CommandExecutor {

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
                    if (!MagicAssistant.rideManager.isSign(b.getLocation())) {
                        continue;
                    }
                    Sign s = (Sign) b.getState();
                    if (correct(s.getLine(0))) {
                        s.setLine(0, "[" + s.getLine(0) + "]");
                        s.update();
                    }
                }
            }
        }
        return true;
    }

    private boolean correct(String s) {
        return s.equals("+train") || s.equals("train") || s.equals("+cart") || s.equals("cart") || s.equals("!train") || s.equals("!cart");
    }
}