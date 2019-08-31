package network.palace.parkmanager.commands.shop.item;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.utils.MiscUtil;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.handlers.shop.Shop;
import network.palace.parkmanager.handlers.shop.ShopItem;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

@CommandMeta(description = "List shop items")
public class ListCommand extends CoreCommand {

    public ListCommand() {
        super("list");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "/shop item list [shop id]");
            return;
        }
        if (!MiscUtil.checkIfInt(args[0])) {
            player.sendMessage(ChatColor.RED + args[0] + " is not an integer!");
            return;
        }
        int shopId = Integer.parseInt(args[0]);
        Shop shop = ParkManager.getShopManager().getShop(shopId);
        if (shop == null) {
            player.sendMessage(ChatColor.RED + "Could not find a shop by id " + shopId + "!");
            return;
        }

        player.sendMessage(ChatColor.GREEN + "Shop Items:");
        for (ShopItem shopItem : shop.getItems()) {
            ItemStack item = shopItem.getItem();
            player.sendMessage(ChatColor.AQUA + "- [" + shopItem.getId() + "] " + ChatColor.YELLOW + item.getItemMeta().getDisplayName() +
                    " (" + item.getType().name().toLowerCase() + ") " + shopItem.getCurrencyType().getIcon() + shopItem.getCost());
        }
    }
}
