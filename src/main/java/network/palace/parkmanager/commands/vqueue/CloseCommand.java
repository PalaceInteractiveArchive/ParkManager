package network.palace.parkmanager.commands.vqueue;

import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;

@CommandMeta(description = "Close a virtual queue hosted on this server")
public class CloseCommand extends CoreCommand {

    public CloseCommand() {
        super("close");
    }
}
