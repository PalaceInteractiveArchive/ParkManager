package network.palace.parkmanager.commands;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CommandPermission;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandMeta(description = "View a player's enderchest")
@CommandPermission(rank = Rank.MOD)
public class EnderchestCommand extends CoreCommand {

    public EnderchestCommand() {
        super("enderchest");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        if (args.length == 1) {
            Player tp = Bukkit.getPlayer(args[0]);
            if (tp == null) {
                player.sendMessage(ChatColor.RED + "Player not found!");
                return;
            }
            player.sendMessage(ChatColor.GREEN + "Now looking in " + tp.getName() + "'s Enderchest!");
            player.openInventory(tp.getEnderChest());
            return;
        }
        player.sendMessage(ChatColor.RED + "/enderchest [Username]");
    }
}
