package network.palace.parkmanager.commands.vqueue;

import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;

@CommandMeta(description = "Remove a virtual queue hosted on this server")
public class RemoveCommand extends CoreCommand {

    public RemoveCommand() {
        super("remove");
    }
}
