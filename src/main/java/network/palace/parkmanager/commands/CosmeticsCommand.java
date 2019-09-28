package network.palace.parkmanager.commands;

import network.palace.core.command.CommandException;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.cosmetics.Cosmetics;

public class CosmeticsCommand extends CoreCommand {

    public CosmeticsCommand() {
        super("cosmetics");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        Cosmetics.getInstance().openCosmeticsInventory(player);
    }
}
