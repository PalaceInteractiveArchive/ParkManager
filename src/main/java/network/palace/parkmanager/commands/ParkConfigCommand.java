package network.palace.parkmanager.commands;

import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.Rank;
import network.palace.parkmanager.commands.config.JoinMessageCommand;
import network.palace.parkmanager.commands.config.SpawnCommand;
import network.palace.parkmanager.commands.config.SpawnOnJoinCommand;
import network.palace.parkmanager.commands.config.WarpOnJoinCommand;

@CommandMeta(description = "Manage park config settings", rank = Rank.MOD)
public class ParkConfigCommand extends CoreCommand {

    public ParkConfigCommand() {
        super("parkconfig");
        registerSubCommand(new SpawnCommand());
        registerSubCommand(new JoinMessageCommand());
        registerSubCommand(new SpawnOnJoinCommand());
        registerSubCommand(new WarpOnJoinCommand());
    }

    @Override
    protected boolean isUsingSubCommandsOnly() {
        return true;
    }
}
