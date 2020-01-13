package network.palace.parkmanager.commands.shop.outfit;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.handlers.outfits.Outfit;
import network.palace.parkmanager.handlers.shop.Shop;
import network.palace.parkmanager.handlers.shop.ShopOutfit;
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
        Shop shop = ParkManager.getShopManager().getShopById(args[0]);
        if (shop == null) {
            player.sendMessage(ChatColor.RED + "Could not find a shop by id " + args[0] + "!");
            return;
        }

        player.sendMessage(ChatColor.GREEN + "Shop Outfits:");
        for (ShopOutfit shopOutfit : shop.getOutfits()) {
            Outfit outfit = ParkManager.getWardrobeManager().getOutfit(shopOutfit.getOutfitId());
            if (outfit == null) continue;
            player.sendMessage(ChatColor.AQUA + "- [" + shopOutfit.getId() + "] " + ChatColor.YELLOW + outfit.getName() + " " + shopOutfit.getCurrencyType().getIcon() + shopOutfit.getCost());
        }
    }
}
