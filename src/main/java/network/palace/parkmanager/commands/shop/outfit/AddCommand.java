package network.palace.parkmanager.commands.shop.outfit;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.utils.MiscUtil;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.handlers.outfits.Outfit;
import network.palace.parkmanager.handlers.shop.Shop;
import network.palace.parkmanager.handlers.shop.ShopOutfit;
import org.bukkit.ChatColor;

@CommandMeta(description = "Add a new shop outfit")
public class AddCommand extends CoreCommand {

    public AddCommand() {
        super("add");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        if (args.length < 3) {
            player.sendMessage(ChatColor.RED + "/shop outfit add [shop id] [outfit id] [cost in tokens]");
            return;
        }
        if (!MiscUtil.checkIfInt(args[1])) {
            player.sendMessage(ChatColor.RED + args[1] + " is not an integer!");
            return;
        }
        if (!MiscUtil.checkIfInt(args[2])) {
            player.sendMessage(ChatColor.RED + args[2] + " is not an integer!");
            return;
        }
        Shop shop = ParkManager.getShopManager().getShopById(args[0]);
        if (shop == null) {
            player.sendMessage(ChatColor.RED + "Could not find a shop by id " + args[0] + "!");
            return;
        }

        int outfitId = Integer.parseInt(args[1]);
        Outfit outfit = ParkManager.getWardrobeManager().getOutfit(outfitId);
        if (outfit == null) {
            player.sendMessage(ChatColor.RED + "Couldn't find an outfit with id " + outfitId + "!");
            return;
        }

        int cost = Integer.parseInt(args[2]);
        if (cost < 0) {
            player.sendMessage(ChatColor.RED + "Cost cannot be negative!");
            return;
        }

        shop.addOutfit(new ShopOutfit(outfitId, cost));
        ParkManager.getShopManager().saveToFile();

        player.sendMessage(ChatColor.GREEN + "Added the " + outfit.getName() + ChatColor.GREEN + " outfit to " + shop.getName() + "!");
    }
}
