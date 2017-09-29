package network.palace.parkmanager.commands;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CommandPermission;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.parkmanager.ParkManager;
import org.bukkit.ChatColor;

/**
 * Created by Marc on 10/25/15
 */
@CommandMeta(description = "Go to previous location")
@CommandPermission(rank = Rank.MOD)
public class Commandback extends CoreCommand {

    public Commandback() {
        super("back");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        if (!ParkManager.getInstance().getTeleportUtil().back(player)) {
            player.sendMessage(ChatColor.GRAY + "No location to teleport back to!");
        }
    }
}