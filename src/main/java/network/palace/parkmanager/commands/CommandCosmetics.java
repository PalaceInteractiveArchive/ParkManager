package network.palace.parkmanager.commands;

import network.palace.core.command.CommandException;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.cosmetics.Cosmetics;

/**
 * @author Innectic
 * @since 6/23/2017
 */
public class CommandCosmetics extends CoreCommand {

    public CommandCosmetics() {
        super("cosmetics");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        Cosmetics.getInstance().getCosmeticsInventory().open(player);
    }

}
