package network.palace.parkmanager.commands;

import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.Rank;
import network.palace.parkmanager.commands.spawn.SpawnCommand;
import network.palace.parkmanager.commands.spawn.SpawnOnJoinCommand;
import network.palace.parkmanager.commands.spawn.WarpOnJoinCommand;

@CommandMeta(description = "Manage spawn settings", rank = Rank.MOD)
public class SetSpawnCommand extends CoreCommand {

    public SetSpawnCommand() {
        super("setspawn");
        registerSubCommand(new SpawnCommand());
        registerSubCommand(new SpawnOnJoinCommand());
        registerSubCommand(new WarpOnJoinCommand());
    }

    @Override
    protected boolean isUsingSubCommandsOnly() {
        return true;
    }
}
