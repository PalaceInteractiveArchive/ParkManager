package network.palace.parkmanager.commands.shop;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.utils.MiscUtil;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.handlers.shop.Shop;
import org.bukkit.ChatColor;

@CommandMeta(description = "Remove an existing shops")
public class RemoveCommand extends CoreCommand {

    public RemoveCommand() {
        super("remove");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "/shop remove [id]");
            player.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + "Get the shop id from /shop list!");
            return;
        }
        if (!MiscUtil.checkIfInt(args[0])) {
            player.sendMessage(ChatColor.RED + args[0] + " is not an integer!");
            return;
        }
        int id = Integer.parseInt(args[0]);
        Shop shop = ParkManager.getShopManager().getShop(id);
        if (shop == null) {
            player.sendMessage(ChatColor.RED + "Could not find a shop by id " + id + "!");
            return;
        }
        if (ParkManager.getShopManager().removeShop(id)) {
            player.sendMessage(ChatColor.GREEN + "Successfully removed " + shop.getName() + "!");
        } else {
            player.sendMessage(ChatColor.RED + "There was an error removing " + shop.getName() + "!");
        }
    }
}
