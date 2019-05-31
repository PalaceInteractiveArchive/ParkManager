package network.palace.parkmanager.commands.shop;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.shop.Shop;
import org.bukkit.ChatColor;

@CommandMeta(description = "List all shops")
public class ListCommand extends CoreCommand {

    public ListCommand() {
        super("list");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        player.sendMessage(ChatColor.GREEN + "Shops:");
        for (Shop shop : ParkManager.getShopManager().getShops()) {
            player.sendMessage(ChatColor.AQUA + "- [" + shop.getId() + "] " + ChatColor.YELLOW + shop.getName() + ChatColor.GREEN + " at " + ChatColor.YELLOW + "/warp " + shop.getWarp());
        }
    }
}
