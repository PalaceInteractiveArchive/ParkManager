package network.palace.parkmanager.commands.shop.item;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.economy.currency.CurrencyType;
import network.palace.core.player.CPlayer;
import network.palace.core.utils.MiscUtil;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.handlers.Park;
import network.palace.parkmanager.handlers.shop.Shop;
import network.palace.parkmanager.handlers.shop.ShopItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

@CommandMeta(description = "Add a new shop item")
public class AddCommand extends CoreCommand {

    public AddCommand() {
        super("add");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        if (args.length < 4) {
            player.sendMessage(ChatColor.RED + "/shop item add [shop id] [cost] [balance/tokens] [display name]");
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
        ItemStack item = player.getItemInMainHand().clone();
        if (item == null || item.getType() == null || item.getType().equals(Material.AIR)) {
            player.sendMessage(ChatColor.RED + "Hold the shop item in your hand!");
            return;
        }
        Shop shop = ParkManager.getShopManager().getShopById(args[0], park.getId());
        if (shop == null) {
            player.sendMessage(ChatColor.RED + "Could not find a shop by id " + args[0] + "!");
            return;
        }

        int cost = Integer.parseInt(args[1]);
        StringBuilder displayName = new StringBuilder();
        for (int i = 3; i < args.length; i++) {
            displayName.append(args[i]);
            if (i < (args.length - 1)) {
                displayName.append(" ");
            }
        }
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName.toString()));
        item.setItemMeta(meta);

        shop.addItem(new ShopItem(shop.nextId(), item, cost, CurrencyType.fromString(args[2])));
        ParkManager.getShopManager().saveToFile();

        player.sendMessage(ChatColor.GREEN + "Added a new item to " + shop.getName() + ChatColor.GREEN + " named " + ChatColor.translateAlternateColorCodes('&', displayName.toString()) + "!");
    }
}
