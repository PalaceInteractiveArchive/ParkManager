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

@CommandMeta(description = "Remove a shop outfit")
public class RemoveCommand extends CoreCommand {

    public RemoveCommand() {
        super("remove");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "/shop outfit remove [shop id] [shop outfit id]");
            player.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + "Get the outfit id from /shop outfit list [shop id]!");
            return;
        }
        if (!MiscUtil.checkIfInt(args[0])) {
            player.sendMessage(ChatColor.RED + args[0] + " is not an integer!");
            return;
        }
        if (!MiscUtil.checkIfInt(args[1])) {
            player.sendMessage(ChatColor.RED + args[1] + " is not an integer!");
            return;
        }
        int shopId = Integer.parseInt(args[0]);
        Shop shop = ParkManager.getShopManager().getShop(shopId);
        if (shop == null) {
            player.sendMessage(ChatColor.RED + "Could not find a shop by id " + shopId + "!");
            return;
        }

        int shopOutfitId = Integer.parseInt(args[1]);
        ShopOutfit shopOutfit = shop.getOutfit(shopOutfitId);
        if (shopOutfit == null) {
            player.sendMessage(ChatColor.RED + "Could not find a shop outfit by id " + shopOutfitId + "!");
            return;
        }

        shop.removeOutfit(shopOutfitId);
        ParkManager.getShopManager().saveToFile();
        Outfit outfit = ParkManager.getWardrobeManager().getOutfit(shopOutfit.getOutfitId());
        if (outfit == null) {
            player.sendMessage(ChatColor.GREEN + "Removed the outfit " + shopOutfitId + ChatColor.GREEN + " from " + shop.getName() + "!");
        } else {
            player.sendMessage(ChatColor.GREEN + "Removed the outfit " + outfit.getName() + ChatColor.GREEN + " from " + shop.getName() + "!");
        }
    }
}
