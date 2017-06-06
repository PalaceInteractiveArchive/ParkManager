package network.palace.parkmanager.commands;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.parkmanager.autograph.AutographInventory;
import org.bukkit.ChatColor;

/**
 * @author Innectic
 * @since 5/26/2017
 */
@CommandMeta(description = "Switch your current autograph bok")
public class CommandAutoBook extends CoreCommand {

    public CommandAutoBook() {
        super("autobook");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        player.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "Opening menu...");
        AutographInventory inventory = new AutographInventory(player);
        inventory.open();
    }

    /**
     * Display some useful help for the dumb players that can't command
     *
     * @param player the player to show
     */
    private void helpMenu(CPlayer player) {
        player.sendMessage(ChatColor.RED + "/autobook " + ChatColor.YELLOW + "- Brings up an inventory with all the books to choose from.");
    }
}
