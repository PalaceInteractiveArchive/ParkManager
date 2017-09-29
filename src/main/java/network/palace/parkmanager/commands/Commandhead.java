package network.palace.parkmanager.commands;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CommandPermission;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.Rank;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * Created by Marc on 3/10/15
 */
@CommandMeta(description = "Get a player head")
@CommandPermission(rank = Rank.MOD)
public class Commandhead extends CoreCommand {

    public Commandhead() {
        super("head");
    }

    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        sender.sendMessage(ChatColor.RED + "This command has been disabled. Visit " + ChatColor.AQUA +
                "heads.legobuilder.net " + ChatColor.RED + "or " + ChatColor.AQUA + "skins.legobuilder.net " +
                ChatColor.RED + "to get player heads.");
    }
}
