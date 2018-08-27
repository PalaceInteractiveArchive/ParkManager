package network.palace.parkmanager.commands;

import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.Rank;
import network.palace.parkmanager.commands.mural.CreateMural;
import network.palace.parkmanager.commands.mural.ListMural;
import network.palace.parkmanager.commands.mural.ReloadMural;

@CommandMeta(description = "Manage murals", rank = Rank.MOD)
public class MuralCommand extends CoreCommand {

    public MuralCommand() {
        super("mural");
        registerSubCommand(new CreateMural());
        registerSubCommand(new ListMural());
        registerSubCommand(new ReloadMural());
    }

    @Override
    protected boolean isUsingSubCommandsOnly() {
        return true;
    }
}
