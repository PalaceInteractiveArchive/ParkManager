package network.palace.parkmanager.commands.attractions;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.attractions.Attraction;
import network.palace.parkmanager.handlers.AttractionCategory;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

@CommandMeta(description = "Create a new attraction")
public class CreateCommand extends CoreCommand {

    public CreateCommand() {
        super("create");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        if (args.length < 4) {
            player.sendMessage(ChatColor.RED + "/attraction create [id] [warp] [category1,category2] [name]");
            player.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.ITALIC + "Also, hold the item for the attraction in your hand!");
            player.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.ITALIC + "The name of the item will be changed to the name of the attraction.");
            player.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.ITALIC + "For a list of categories, run /attraction categories");
            return;
        }
        if (ParkManager.getAttractionManager().getAttraction(args[0]) != null) {
            player.sendMessage(ChatColor.RED + "An attraction already exists with the id " + args[0] + "!");
            return;
        }
        ItemStack item = player.getItemInMainHand().clone();
        if (item == null || item.getType() == null || item.getType().equals(Material.AIR)) {
            player.sendMessage(ChatColor.RED + "Hold the item in your hand that will represent the attraction in the menu!");
            return;
        }
        StringBuilder name = new StringBuilder();
        for (int i = 3; i < args.length; i++) {
            name.append(args[i]).append(" ");
        }
        String displayName = ChatColor.AQUA + ChatColor.translateAlternateColorCodes('&', name.toString().trim());
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(displayName);
        item.setItemMeta(meta);

        List<AttractionCategory> categories = new ArrayList<>();
        for (String s : args[2].split(",")) {
            AttractionCategory category = AttractionCategory.fromString(s);
            if (category == null) {
                player.sendMessage(ChatColor.RED + "Unknown category '" + s + "'!");
                continue;
            }
            categories.add(category);
        }

        ParkManager.getAttractionManager().addAttraction(new Attraction(args[0], displayName, args[1], "",
                categories, true, item, null));
        player.sendMessage(ChatColor.GREEN + "Created new attraction " + displayName + ChatColor.GREEN + " at /warp " + args[1] + "!");
    }
}
