package network.palace.parkmanager.commands;

import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.Rank;
import network.palace.parkmanager.commands.config.SpawnCommand;
import network.palace.parkmanager.commands.config.*;

@CommandMeta(description = "Manage park config settings", rank = Rank.CM)
public class ParkConfigCommand extends CoreCommand {

    public ParkConfigCommand() {
        super("parkconfig");
        registerSubCommand(new JoinMessageCommand());
        registerSubCommand(new ParkCommand());
        registerSubCommand(new SpawnCommand());
        registerSubCommand(new SpawnOnJoinCommand());
        registerSubCommand(new WarpOnJoinCommand());
    }

    @Override
    protected boolean isUsingSubCommandsOnly() {
        return true;
    }
}
