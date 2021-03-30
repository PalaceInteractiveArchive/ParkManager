package network.palace.parkmanager.commands;

import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.Rank;
import network.palace.parkmanager.commands.vqueue.*;

@CommandMeta(rank = Rank.CM, aliases = "vq")
public class VirtualQueueCommand extends CoreCommand {

    public VirtualQueueCommand() {
        super("vqueue");
        registerSubCommand(new CreateCommand());
        registerSubCommand(new OpenCommand());
        registerSubCommand(new CloseCommand());
        registerSubCommand(new AnnounceCommand());
        registerSubCommand(new AdvanceCommand());
        registerSubCommand(new ListCommand());
        registerSubCommand(new RemoveCommand());
    }

    @Override
    protected boolean isUsingSubCommandsOnly() {
        return true;
    }
}
