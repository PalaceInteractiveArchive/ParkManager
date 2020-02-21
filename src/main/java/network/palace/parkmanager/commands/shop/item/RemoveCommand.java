package network.palace.parkmanager.commands.shop.item;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.utils.MiscUtil;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.handlers.Park;
import network.palace.parkmanager.handlers.shop.Shop;
import network.palace.parkmanager.handlers.shop.ShopItem;
import org.bukkit.ChatColor;

@CommandMeta(description = "Remove a shop item")
public class RemoveCommand extends CoreCommand {

    public RemoveCommand() {
        super("remove");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "/shop item remove [shop id] [shop item id]");
            player.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + "Get the shop item id from /shop item list [shop id]!");
            return;
        }
        Park park = ParkManager.getParkUtil().getPark(player.getLocation());
        if (park == null) {
            player.sendMessage(ChatColor.RED + "You must be inside a park when running this command!");
            return;
        }
        if (!MiscUtil.checkIfInt(args[1])) {
            player.sendMessage(ChatColor.RED + args[1] + " is not an integer!");
            return;
        }
        Shop shop = ParkManager.getShopManager().getShopById(args[0], park.getId());
        if (shop == null) {
            player.sendMessage(ChatColor.RED + "Could not find a shop by id " + args[0] + "!");
            return;
        }

        int itemId = Integer.parseInt(args[1]);
        ShopItem item = shop.getItem(itemId);
        if (item == null) {
            player.sendMessage(ChatColor.RED + "Could not find a shop item by id " + itemId + "!");
            return;
        }

        shop.removeItem(itemId);
        ParkManager.getShopManager().saveToFile();

        player.sendMessage(ChatColor.GREEN + "Removed the item " + item.getItem().getItemMeta().getDisplayName()
                + ChatColor.GREEN + " (" + item.getItem().getType().name().toLowerCase() + ") from " + shop.getName() + "!");
    }
}
