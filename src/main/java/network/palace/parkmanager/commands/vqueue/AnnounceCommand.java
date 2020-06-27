package network.palace.parkmanager.commands.vqueue;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;

@CommandMeta(description = "Announce a virtual queue hosted on this server")
public class AnnounceCommand extends CoreCommand {

    public AnnounceCommand() {
        super("announce");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
    }
}
