package network.palace.parkmanager.commands;

import network.palace.core.Core;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.parkmanager.handlers.storage.StorageData;
import org.bukkit.ChatColor;

@CommandMeta(description = "Look into a player's inventory", rank = Rank.CM)
public class InvSeeCommand extends CoreCommand {

    public InvSeeCommand() {
        super("invsee");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "/invsee [username] [main/backpack/locker]");
            return;
        }
        CPlayer target = Core.getPlayerManager().getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(ChatColor.RED + "Player not found!");
            return;
        }
        switch (args[1].toLowerCase()) {
            case "main": {
                player.openInventory(target.getInventory());
                return;
            }
            case "backpack": {
                if (!target.getRegistry().hasEntry("storageData")) {
                    player.sendMessage(ChatColor.RED + "There was an error opening " + target.getName() + "'s backpack!");
                    return;
                }
                StorageData data = (StorageData) target.getRegistry().getEntry("storageData");
                player.openInventory(data.getBackpack());
                return;
            }
            case "locker": {
                if (!target.getRegistry().hasEntry("storageData")) {
                    player.sendMessage(ChatColor.RED + "There was an error opening " + target.getName() + "'s locker!");
                    return;
                }
                StorageData data = (StorageData) target.getRegistry().getEntry("storageData");
                player.openInventory(data.getLocker());
                return;
            }
        }
        player.sendMessage(ChatColor.RED + "/invsee [username] [main/backpack/locker]");
    }
}
