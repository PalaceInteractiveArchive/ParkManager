package network.palace.parkmanager.commands;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;

@CommandMeta(description = "Set time to day", rank = Rank.CM)
public class DayCommand extends CoreCommand {

    public DayCommand() {
        super("day");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        player.getWorld().setTime(1000);
        player.sendMessage(ChatColor.GRAY + "Time has been set to " + ChatColor.GREEN + "Day " + ChatColor.GRAY + "in world " +
                ChatColor.GREEN + player.getWorld().getName() + ".");
    }

    @Override
    protected void handleCommand(BlockCommandSender commandSender, String[] args) throws CommandException {
        commandSender.getBlock().getWorld().setTime(1000);
        commandSender.sendMessage(ChatColor.GRAY + "Time has been set to " + ChatColor.GREEN + "Day " + ChatColor.GRAY + "in world " +
                ChatColor.GREEN + commandSender.getBlock().getWorld().getName() + ".");
    }

    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        for (World world : Bukkit.getWorlds()) {
            world.setTime(1000);
            sender.sendMessage(ChatColor.GRAY + "Time has been set to " + ChatColor.GREEN + "Day " + ChatColor.GRAY + "in world " +
                    ChatColor.GREEN + world.getName() + ".");
        }
    }
}
