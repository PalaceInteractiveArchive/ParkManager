package network.palace.parkmanager.commands;

import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.Rank;
import network.palace.parkmanager.commands.kiosk.CreateCommand;
import network.palace.parkmanager.commands.kiosk.DeleteCommand;

@CommandMeta(description = "Default kiosk command", rank = Rank.CM)
public class KioskCommand extends CoreCommand {

    public KioskCommand() {
        super("kiosk");
        registerSubCommand(new CreateCommand());
        registerSubCommand(new DeleteCommand());
    }

    @Override
    protected boolean isUsingSubCommandsOnly() {
        return true;
    }
}
