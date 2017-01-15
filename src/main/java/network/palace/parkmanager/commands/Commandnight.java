package network.palace.parkmanager.commands;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CommandPermission;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.Rank;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

@CommandMeta(description = "Set time to night")
@CommandPermission(rank = Rank.KNIGHT)
public class Commandnight extends CoreCommand {

    public Commandnight() {
        super("night");
    }

    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        Bukkit.getWorlds().get(0).setTime(16000);
        sender.sendMessage(ChatColor.GRAY + "Time has been set to " + ChatColor.GREEN + "Night.");
    }
}
