package network.palace.parkmanager.commands.kiosk;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.parkmanager.ParkManager;

@CommandMeta(description = "Create a new kiosk where you're standing")
public class CreateCommand extends CoreCommand {

    public CreateCommand() {
        super("create");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        ParkManager.getFastPassKioskManager().spawn(player);
    }
}
