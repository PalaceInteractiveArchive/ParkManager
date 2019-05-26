package network.palace.parkmanager.commands.attractions;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.attractions.Attraction;
import org.bukkit.ChatColor;

@CommandMeta(description = "List all attractions")
public class ListCommand extends CoreCommand {

    public ListCommand() {
        super("list");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        player.sendMessage(ChatColor.GREEN + "Attractions:");
        for (Attraction attraction : ParkManager.getAttractionManager().getAttractions()) {
            player.sendMessage(ChatColor.AQUA + "- [" + attraction.getId() + "] " + ChatColor.YELLOW + attraction.getName() + ChatColor.GREEN + " at " + ChatColor.YELLOW + "/warp " + attraction.getWarp());
        }
    }
}
