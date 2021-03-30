package network.palace.parkmanager.commands;

import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.Rank;
import network.palace.parkmanager.commands.showschedule.EditCommand;
import network.palace.parkmanager.commands.showschedule.UpdateCommand;

@CommandMeta(description = "Show schedule command", rank = Rank.CM)
public class ShowScheduleCommand extends CoreCommand {

    public ShowScheduleCommand() {
        super("showschedule");
        registerSubCommand(new EditCommand());
        registerSubCommand(new UpdateCommand());
    }

    @Override
    protected boolean isUsingSubCommandsOnly() {
        return true;
    }
}
