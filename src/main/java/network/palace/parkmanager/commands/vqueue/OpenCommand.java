package network.palace.parkmanager.commands.vqueue;

import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;

@CommandMeta(description = "Open a virtual queue hosted on this server")
public class OpenCommand extends CoreCommand {

    public OpenCommand() {
        super("open");
    }
}
