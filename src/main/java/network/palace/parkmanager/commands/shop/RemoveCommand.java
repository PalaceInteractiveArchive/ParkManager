package network.palace.parkmanager.commands.shop;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.handlers.Park;
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
        Park park = ParkManager.getParkUtil().getPark(player.getLocation());
        if (park == null) {
            player.sendMessage(ChatColor.RED + "You must be inside a park when running this command!");
            return;
        }
        Shop shop = ParkManager.getShopManager().getShopById(args[0], park.getId());
        if (shop == null) {
            player.sendMessage(ChatColor.RED + "Could not find a shop by id " + args[0] + "!");
            return;
        }
        if (ParkManager.getShopManager().removeShop(args[0], park.getId())) {
            player.sendMessage(ChatColor.GREEN + "Successfully removed " + shop.getName() + "!");
        } else {
            player.sendMessage(ChatColor.RED + "There was an error removing " + shop.getName() + "!");
        }
    }
}
