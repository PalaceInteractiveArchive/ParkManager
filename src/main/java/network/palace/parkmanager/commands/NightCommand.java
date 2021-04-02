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

@CommandMeta(description = "Set time to night", rank = Rank.CM)
public class NightCommand extends CoreCommand {

    public NightCommand() {
        super("night");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        player.getWorld().setTime(16000);
        player.sendMessage(ChatColor.GRAY + "Time has been set to " + ChatColor.GREEN + "Night " + ChatColor.GRAY + "in world " +
                ChatColor.GREEN + player.getWorld().getName() + ".");
    }

    @Override
    protected void handleCommand(BlockCommandSender commandSender, String[] args) throws CommandException {
        commandSender.getBlock().getWorld().setTime(16000);
        commandSender.sendMessage(ChatColor.GRAY + "Time has been set to " + ChatColor.GREEN + "Night " + ChatColor.GRAY + "in world " +
                ChatColor.GREEN + commandSender.getBlock().getWorld().getName() + ".");
    }

    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        for (World world : Bukkit.getWorlds()) {
            world.setTime(16000);
            sender.sendMessage(ChatColor.GRAY + "Time has been set to " + ChatColor.GREEN + "Night " + ChatColor.GRAY + "in world " +
                    ChatColor.GREEN + world.getName() + ".");
        }
    }
}
