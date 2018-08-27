package network.palace.parkmanager.commands;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.Rank;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * Created by Marc on 3/10/15
 */
@CommandMeta(description = "Get a player head", rank = Rank.MOD)
public class HeadCommand extends CoreCommand {

    public HeadCommand() {
        super("head");
    }

    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        sender.sendMessage(ChatColor.RED + "This command has been disabled. Visit " + ChatColor.AQUA +
                "heads.legobuilder.me " + ChatColor.RED + "or " + ChatColor.AQUA + "skins.legobuilder.me " +
                ChatColor.RED + "to get player heads.");
    }
}
