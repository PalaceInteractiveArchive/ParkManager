package network.palace.parkmanager.commands.attractions;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.attractions.Attraction;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

@CommandMeta(description = "Create a new attraction")
public class CreateCommand extends CoreCommand {

    public CreateCommand() {
        super("create");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "/attraction create [warp] [name]");
            player.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + "Also, hold the item for the attraction in your hand!");
            player.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + "The name of the item will be changed to the name of the attraction.");
            return;
        }
        ItemStack item = player.getItemInMainHand().clone();
        if (item == null || item.getType() == null || item.getType().equals(Material.AIR)) {
            player.sendMessage(ChatColor.RED + "Hold the item in your hand that will represent the attraction in the menu!");
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
        ParkManager.getAttractionManager().addAttraction(new Attraction(ParkManager.getAttractionManager().getNextId(), displayName, args[0], item));
        player.sendMessage(ChatColor.GREEN + "Created new attraction " + displayName + ChatColor.GREEN + " at /warp " + args[0] + "!");
    }
}
