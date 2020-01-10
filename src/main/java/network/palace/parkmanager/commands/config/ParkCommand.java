package network.palace.parkmanager.commands.config;

import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.parkmanager.commands.config.park.CreateCommand;
import network.palace.parkmanager.commands.config.park.ListCommand;
import network.palace.parkmanager.commands.config.park.ReloadCommand;
import network.palace.parkmanager.commands.config.park.RemoveCommand;

@CommandMeta(description = "Manage local parks")
public class ParkCommand extends CoreCommand {

    public ParkCommand() {
        super("park");
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
