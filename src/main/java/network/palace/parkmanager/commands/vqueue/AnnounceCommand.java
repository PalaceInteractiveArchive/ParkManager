package network.palace.parkmanager.commands.vqueue;

import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;

@CommandMeta(description = "Announce a virtual queue hosted on this server")
public class AnnounceCommand extends CoreCommand {

    public AnnounceCommand() {
        super("announce");
    }
}
