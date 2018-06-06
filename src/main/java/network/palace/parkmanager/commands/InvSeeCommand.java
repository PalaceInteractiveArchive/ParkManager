package network.palace.parkmanager.commands;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CommandPermission;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.handlers.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandMeta(description = "Look into a player's inventory")
@CommandPermission(rank = Rank.MOD)
public class InvSeeCommand extends CoreCommand {

    public InvSeeCommand() {
        super("invsee");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        if (args.length == 2) {
            Player tp = Bukkit.getPlayer(args[0]);
            if (tp == null) {
                player.sendMessage(ChatColor.RED + "Player not found!");
                return;
            }
            String type = args[1];
            PlayerData data = ParkManager.getInstance().getPlayerData(tp.getUniqueId());
            if (data != null && data.getLocker() != null && data.getBackpack() != null) {
                switch (type.toLowerCase()) {
                    case "backpack": {
                        player.sendMessage(ChatColor.GREEN + "Now looking in " + tp.getName() + "'s Backpack!");
                        player.openInventory(data.getBackpack().getInventory());
                        return;
                    }
                    case "locker": {
                        player.sendMessage(ChatColor.GREEN + "Now looking in " + tp.getName() + "'s Locker!");
                        player.openInventory(data.getLocker().getInventory());
                        return;
                    }
                }
            } else {
                player.sendMessage(ChatColor.RED + "Player data not found for " + args[0] + ", showing main inventory");
            }
            player.sendMessage(ChatColor.GREEN + "Now looking in " + tp.getName() + "'s Main Inventory!");
            player.openInventory(tp.getInventory());
            return;
        }
        player.sendMessage(ChatColor.RED + "/invsee [Username] [Main/Backpack/Locker]");
    }
}
