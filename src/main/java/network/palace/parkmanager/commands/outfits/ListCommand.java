package network.palace.parkmanager.commands.outfits;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.handlers.outfits.Outfit;
import org.bukkit.ChatColor;

@CommandMeta(description = "List all outfits")
public class ListCommand extends CoreCommand {

    public ListCommand() {
        super("list");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        player.sendMessage(ChatColor.GREEN + "Outfits:");
        for (Outfit outfit : ParkManager.getWardrobeManager().getOutfits()) {
            player.sendMessage(ChatColor.AQUA + "- [" + outfit.getId() + "] " + ChatColor.YELLOW + outfit.getName());
        }
    }
}
