package network.palace.parkmanager.commands;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.parkmanager.ParkManager;
import org.bukkit.ChatColor;

@CommandMeta(description = "Go to your previous location", rank = Rank.CM)
public class BackCommand extends CoreCommand {

    public BackCommand() {
        super("back");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        if (!ParkManager.getTeleportUtil().back(player)) {
            player.sendMessage(ChatColor.GRAY + "No location to teleport back to!");
        }
    }
}