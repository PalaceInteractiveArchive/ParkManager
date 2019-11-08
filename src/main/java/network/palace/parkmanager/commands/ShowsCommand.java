package network.palace.parkmanager.commands;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import org.bukkit.ChatColor;

@CommandMeta(description = "Allow Shareholders to run Shows with staff approval")
public class ShowsCommand extends CoreCommand {

    public ShowsCommand() {
        super("shows");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        if (player.getRank().getRankId() < Rank.SHAREHOLDER.getRankId()) {
            player.sendMessage(ChatColor.AQUA + "You must be a " + Rank.SHAREHOLDER.getFormattedName() + ChatColor.AQUA +
                    " can use this! Find out more info at " + ChatColor.GREEN + "https://palnet.us/shareholder");
            return;
        }
    }
}
