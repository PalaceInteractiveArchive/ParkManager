package network.palace.parkmanager.commands;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.core.utils.MiscUtil;
import network.palace.parkmanager.ParkManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.BlockCommandSender;

@CommandMeta(description = "Delay placing a redstone block", rank = Rank.MOD)
public class DelayCommand extends CoreCommand {

    public DelayCommand() {
        super("delay");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        player.sendMessage(ChatColor.YELLOW + "" + ChatColor.ITALIC + "This command can only be run by command blocks");
        player.sendMessage(ChatColor.RED + "/delay [delay in seconds] x y z");
    }

    @Override
    protected void handleCommand(BlockCommandSender sender, String[] args) throws CommandException {
        if (args.length != 4) {
            sender.sendMessage(ChatColor.RED + "Incorrect amount of arguments!");
            return;
        }
        if (MiscUtil.checkIfDouble(args[0]) && MiscUtil.checkIfDouble(args[1]) && MiscUtil.checkIfDouble(args[2]) && MiscUtil.checkIfDouble(args[3])) {
            int x = (int) Double.parseDouble(args[1]);
            int y = (int) Double.parseDouble(args[2]);
            int z = (int) Double.parseDouble(args[3]);
            Location loc = new Location(sender.getBlock().getWorld(), x, y, z);
            if (!loc.getChunk().isLoaded()) {
                loc.getChunk().load();
            }
            long delay = (long) (20 * (Double.parseDouble(args[0])));
            ParkManager.getDelayUtil().logDelay(loc, delay, Material.REDSTONE_BLOCK);
            return;
        }
        sender.sendMessage(ChatColor.RED + "/delay [delay in seconds] x y z");
    }
}
