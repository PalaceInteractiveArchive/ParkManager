package network.palace.parkmanager.commands;

import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.Rank;
import network.palace.parkmanager.commands.attractions.CreateCommand;
import network.palace.parkmanager.commands.attractions.ListCommand;
import network.palace.parkmanager.commands.attractions.ReloadCommand;
import network.palace.parkmanager.commands.attractions.RemoveCommand;

@CommandMeta(description = "Attraction command", rank = Rank.MOD)
public class AttractionCommand extends CoreCommand {

    public AttractionCommand() {
        super("attraction");
        registerSubCommand(new CreateCommand());
        registerSubCommand(new ListCommand());
        registerSubCommand(new ReloadCommand());
        registerSubCommand(new RemoveCommand());
    }

    @Override
    protected boolean isUsingSubCommandsOnly() {
        return true;
    }
}
