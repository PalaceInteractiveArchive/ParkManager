package network.palace.parkmanager.commands;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CommandPermission;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.Rank;
import network.palace.parkmanager.ParkManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandMeta(description = "Delay placing a block (not recommended)")
@CommandPermission(rank = Rank.KNIGHT)
public class Commanddelay extends CoreCommand {

    public Commanddelay() {
        super("delay");
    }

    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        if (sender instanceof Player) {
            sender.sendMessage(ChatColor.RED + "/delay [delay] x y z");
            return;
        }
        if (args.length != 4) {
            sender.sendMessage(ChatColor.RED + "Incorrect amount of arguments!");
            return;
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
            return;
        }
        sender.sendMessage(ChatColor.RED + "/delay [delay] x y z");
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
