package network.palace.parkmanager.commands.vqueue;

import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;

@CommandMeta(description = "Advance players in line for a virtual queue hosted on this server")
public class AdvanceCommand extends CoreCommand {

    public AdvanceCommand() {
        super("advance");
    }
}
