package network.palace.parkmanager.commands.showschedule;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.parkmanager.ParkManager;
import org.bukkit.ChatColor;

@CommandMeta(description = "Reload the show schedule from the database")
public class UpdateCommand extends CoreCommand {

    public UpdateCommand() {
        super("update");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        player.sendMessage(ChatColor.GREEN + "Updating the show schedule...");
        ParkManager.getScheduleManager().updateShows();
        player.sendMessage(ChatColor.GREEN + "Show schedule updated!");
    }
}
