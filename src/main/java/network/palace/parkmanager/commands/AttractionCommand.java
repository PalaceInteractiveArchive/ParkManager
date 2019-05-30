package network.palace.parkmanager.commands;

import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.Rank;
import network.palace.parkmanager.commands.attractions.*;

@CommandMeta(description = "Attraction command", rank = Rank.MOD)
public class AttractionCommand extends CoreCommand {

    public AttractionCommand() {
        super("attraction");
        registerSubCommand(new CategoriesCommand());
        registerSubCommand(new CloseCommand());
        registerSubCommand(new CreateCommand());
        registerSubCommand(new EditCommand());
        registerSubCommand(new LinkQueueCommand());
        registerSubCommand(new ListCommand());
        registerSubCommand(new OpenCommand());
        registerSubCommand(new ReloadCommand());
        registerSubCommand(new RemoveCommand());
        registerSubCommand(new UnlinkCommand());
    }

    @Override
    protected boolean isUsingSubCommandsOnly() {
        return true;
    }
}
