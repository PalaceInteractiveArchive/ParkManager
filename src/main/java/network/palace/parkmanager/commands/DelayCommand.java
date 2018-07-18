package network.palace.parkmanager.commands;

import network.palace.core.Core;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CommandPermission;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.BlockCommandSender;

@CommandMeta(description = "Delay placing a block")
@CommandPermission(rank = Rank.MOD)
public class DelayCommand extends CoreCommand {

    public DelayCommand() {
        super("delay");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        player.sendMessage(ChatColor.RED + "/delay [delay] x y z");
    }

    @Override
    protected void handleCommand(BlockCommandSender sender, String[] args) throws CommandException {
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
            Core.runTaskLater(() -> {
                b.setType(Material.REDSTONE_BLOCK);
                Core.runTaskLater(() -> b.setType(Material.AIR), 20L);
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
