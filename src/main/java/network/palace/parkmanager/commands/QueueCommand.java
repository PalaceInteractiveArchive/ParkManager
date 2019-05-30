package network.palace.parkmanager.commands;

import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.Rank;
import network.palace.parkmanager.commands.queue.*;

@CommandMeta(description = "Queue command", rank = Rank.MOD)
public class QueueCommand extends CoreCommand {

    public QueueCommand() {
        super("queue");
        registerSubCommand(new CloseCommand());
        registerSubCommand(new CreateCommand());
        registerSubCommand(new EditCommand());
        registerSubCommand(new ListCommand());
        registerSubCommand(new OpenCommand());
        registerSubCommand(new ReloadCommand());
        registerSubCommand(new RemoveCommand());
    }

    @Override
    protected boolean isUsingSubCommandsOnly() {
        return true;
    }
}
