package network.palace.parkmanager.commands.shop.outfit;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.utils.MiscUtil;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.handlers.outfits.Outfit;
import network.palace.parkmanager.handlers.shop.ShopOutfit;
import network.palace.parkmanager.handlers.shop.Shop;
import org.bukkit.ChatColor;

@CommandMeta(description = "List shop outfits")
public class ListCommand extends CoreCommand {

    public ListCommand() {
        super("list");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "/shop outfit list [shop id]");
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

        player.sendMessage(ChatColor.GREEN + "Shop Outfits:");
        for (ShopOutfit shopOutfit : shop.getOutfits()) {
            Outfit outfit = ParkManager.getWardrobeManager().getOutfit(shopOutfit.getOutfitId());
            player.sendMessage(ChatColor.AQUA + "- [" + shopOutfit.getId() + "] " + ChatColor.YELLOW + outfit.getName() + " " + shopOutfit.getCurrencyType().getIcon() + shopOutfit.getCost());
        }
    }
}
