package network.palace.parkmanager.commands.shop;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.handlers.shop.Shop;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

@CommandMeta(description = "Create a new shop")
public class CreateCommand extends CoreCommand {

    public CreateCommand() {
        super("create");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "/shop create [warp] [name]");
            player.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.ITALIC + "Also, hold the item for the shop in your hand!");
            player.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.ITALIC + "The name of the item will be changed to the name of the shop.");
            return;
        }
        ItemStack item = player.getItemInMainHand().clone();
        if (item == null || item.getType() == null || item.getType().equals(Material.AIR)) {
            player.sendMessage(ChatColor.RED + "Hold the item in your hand that will represent the shop in the menu!");
            return;
        }
        StringBuilder name = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            name.append(args[i]).append(" ");
        }
        String displayName = ChatColor.translateAlternateColorCodes('&', name.toString().trim());
        if (!displayName.startsWith(String.valueOf(ChatColor.COLOR_CHAR))) {
            displayName = ChatColor.AQUA + displayName;
        }
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(displayName);
        item.setItemMeta(meta);
        ParkManager.getShopManager().addShop(new Shop(ParkManager.getShopManager().getNextId(), displayName, args[0], item, new ArrayList<>(), new ArrayList<>()));
        player.sendMessage(ChatColor.GREEN + "Created new shop " + displayName + ChatColor.GREEN + " at /warp " + args[0] + "!");
    }
}
