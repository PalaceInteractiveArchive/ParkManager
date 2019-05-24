package network.palace.parkmanager.commands.showschedule;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.parkmanager.ParkManager;
import org.bukkit.ChatColor;

@CommandMeta(description = "Edit the show schedule")
public class EditCommand extends CoreCommand {

    public EditCommand() {
        super("edit");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        player.sendMessage(ChatColor.GREEN + "Opening show schedule edit menu...");
        ParkManager.getScheduleManager().editSchedule(player);
    }
}
