package network.palace.parkmanager.commands.vqueue;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;

@CommandMeta(description = "Advance players in line for a virtual queue hosted on this server")
public class AdvanceCommand extends CoreCommand {

    public AdvanceCommand() {
        super("advance");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {

    }
}
