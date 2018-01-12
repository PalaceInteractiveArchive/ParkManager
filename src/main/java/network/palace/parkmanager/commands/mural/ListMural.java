package network.palace.parkmanager.commands.mural;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.mural.Mural;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

@CommandMeta(description = "List available murals")
public class ListMural extends CoreCommand {

    public ListMural() {
        super("list");
    }

    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        sender.sendMessage(ChatColor.GREEN + "All murals:");
        for (Mural mural : ParkManager.getMuralUtil().getMurals()) {
            sender.sendMessage(ChatColor.AQUA + "- " + mural.getName() + ": " + ChatColor.GREEN + mural.getMinX() +
                    "," + mural.getMinY() + "," + mural.getMinZ() + " to " + mural.getMaxX() + "," + mural.getMaxY() +
                    "," + mural.getMaxZ());
        }
    }
}
