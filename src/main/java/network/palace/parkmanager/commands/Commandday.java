package network.palace.parkmanager.commands;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CommandPermission;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.Rank;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

@CommandMeta(description = "Set time to day")
@CommandPermission(rank = Rank.MOD)
public class Commandday extends CoreCommand {

    public Commandday() {
        super("day");
    }

    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        Bukkit.getWorlds().get(0).setTime(1000);
        sender.sendMessage(ChatColor.GRAY + "Time has been set to " + ChatColor.GREEN + "Day.");
    }
}
