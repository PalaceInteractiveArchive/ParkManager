package network.palace.parkmanager.commands.config.park;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.handlers.Park;
import org.bukkit.ChatColor;

@CommandMeta(description = "List all local parks on this server")
public class ListCommand extends CoreCommand {

    public ListCommand() {
        super("list");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        player.sendMessage(ChatColor.GREEN + "Local Parks:");
        for (Park park : ParkManager.getParkUtil().getParks()) {
            player.sendMessage(ChatColor.AQUA + "- [" + park.getId().name() + "] " + ChatColor.YELLOW + park.getId().getTitle() +
                    ChatColor.GREEN + " in region " + ChatColor.YELLOW + park.getRegion() + ChatColor.GREEN + " on world " + ChatColor.YELLOW + park.getWorld().getName());
        }
    }
}
