package network.palace.parkmanager.commands.shop.outfit;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.utils.MiscUtil;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.handlers.Park;
import network.palace.parkmanager.handlers.outfits.Outfit;
import network.palace.parkmanager.handlers.shop.Shop;
import network.palace.parkmanager.handlers.shop.ShopOutfit;
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
