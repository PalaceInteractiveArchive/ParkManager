package network.palace.parkmanager.commands.attractions;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.parkmanager.handlers.AttractionCategory;
import org.bukkit.ChatColor;

@CommandMeta(description = "List available attraction categories")
public class CategoriesCommand extends CoreCommand {

    public CategoriesCommand() {
        super("categories");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        player.sendMessage(ChatColor.GREEN + "List of attraction categories:");
        for (AttractionCategory category : AttractionCategory.values()) {
            player.sendMessage(ChatColor.AQUA + "- " + ChatColor.YELLOW + category.getShortName());
        }
    }
}
