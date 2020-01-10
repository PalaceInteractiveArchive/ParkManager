package network.palace.parkmanager.commands.food;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.food.FoodLocation;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

@CommandMeta(description = "Create a new food location")
public class CreateCommand extends CoreCommand {

    public CreateCommand() {
        super("create");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        if (args.length < 3) {
            player.sendMessage(ChatColor.RED + "/food create [id] [warp] [name]");
            player.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.ITALIC + "Also, hold the item for the food location in your hand!");
            player.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.ITALIC + "The name of the item will be changed to the name of the food location.");
            return;
        }
        if (ParkManager.getFoodManager().getFoodLocation(args[0]) != null) {
            player.sendMessage(ChatColor.RED + "A food location already exists with the id " + args[0] + "!");
            return;
        }
        ItemStack item = player.getItemInMainHand().clone();
        if (item == null || item.getType() == null || item.getType().equals(Material.AIR)) {
            player.sendMessage(ChatColor.RED + "Hold the item in your hand that will represent the food location in the menu!");
            return;
        }
        StringBuilder name = new StringBuilder();
        for (int i = 2; i < args.length; i++) {
            name.append(args[i]).append(" ");
        }
        String displayName = ChatColor.AQUA + ChatColor.translateAlternateColorCodes('&', name.toString().trim());
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(displayName);
        item.setItemMeta(meta);
        ParkManager.getFoodManager().addFoodLocation(new FoodLocation(args[0], displayName, args[1], item));
        player.sendMessage(ChatColor.GREEN + "Created new food location " + displayName + ChatColor.GREEN + " at /warp " + args[1] + "!");
    }
}
