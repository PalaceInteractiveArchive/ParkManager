package network.palace.parkmanager.commands.park;

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

@CommandMeta(description = "Create a new park")
public class CreateCommand extends CoreCommand {

    public CreateCommand() {
        super("create");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "/parkconfig park create [mk/epcot/dhs/ak/uso] [world] [region]");
            return;
        }
        ItemStack item = player.getItemInMainHand().clone();
        if (item == null || item.getType() == null || item.getType().equals(Material.AIR)) {
            player.sendMessage(ChatColor.RED + "Hold the item in your hand that will represent the park in the menu!");
            return;
        }
        StringBuilder name = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            name.append(args[i]).append(" ");
        }
        String displayName = ChatColor.AQUA + ChatColor.translateAlternateColorCodes('&', name.toString().trim());
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(displayName);
        item.setItemMeta(meta);
        ParkManager.getFoodManager().addFoodLocation(new FoodLocation(ParkManager.getFoodManager().getNextId(), displayName, args[0], item));
        player.sendMessage(ChatColor.GREEN + "Created new park " + displayName + ChatColor.GREEN + " at /warp " + args[0] + "!");
    }
}
