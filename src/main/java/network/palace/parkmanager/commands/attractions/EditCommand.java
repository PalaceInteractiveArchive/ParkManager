package network.palace.parkmanager.commands.attractions;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.utils.MiscUtil;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.attractions.Attraction;
import network.palace.parkmanager.handlers.AttractionCategory;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

@CommandMeta(description = "Edit an existing attraction")
public class EditCommand extends CoreCommand {

    public EditCommand() {
        super("edit");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        // /attraction edit [id] [category1,category2] [warp] [name]
        if (args.length < 4) {
            player.sendMessage(ChatColor.RED + "/attraction edit [id] name [name]");
            player.sendMessage(ChatColor.RED + "/attraction edit [id] warp [warp]");
            player.sendMessage(ChatColor.RED + "/attraction edit [id] description [description] (Color codes are " + ChatColor.ITALIC + "not " + ChatColor.RED + "supported in descriptions!)");
            player.sendMessage(ChatColor.RED + "/attraction edit [id] categories [category1,category2]");
            player.sendMessage(ChatColor.RED + "/attraction edit [id] item");
            return;
        }
        if (!MiscUtil.checkIfInt(args[0])) {
            player.sendMessage(ChatColor.RED + args[0] + " is not an integer!");
            return;
        }
        int id = Integer.parseInt(args[0]);
        Attraction attraction = ParkManager.getAttractionManager().getAttraction(id);
        if (attraction == null) {
            player.sendMessage(ChatColor.RED + "Could not find an attraction by id " + id + "!");
            return;
        }
        switch (args[1].toLowerCase()) {
            case "name": {
                StringBuilder name = new StringBuilder();
                for (int i = 2; i < args.length; i++) {
                    name.append(args[i]).append(" ");
                }
                String displayName = ChatColor.AQUA + ChatColor.translateAlternateColorCodes('&', name.toString().trim());

                player.sendMessage(ChatColor.GREEN + "Set " + attraction.getName() + "'s " + ChatColor.GREEN +
                        "display name to " + ChatColor.YELLOW + displayName);

                attraction.setName(displayName);

                ItemStack item = attraction.getItem();
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(displayName);
                item.setItemMeta(meta);

                ParkManager.getAttractionManager().saveToFile();
                break;
            }
            case "warp": {
                attraction.setWarp(args[2]);

                player.sendMessage(ChatColor.GREEN + "Set " + attraction.getName() + "'s " + ChatColor.GREEN +
                        "warp to " + ChatColor.YELLOW + args[2]);

                ParkManager.getAttractionManager().saveToFile();
                break;
            }
            case "description": {
                StringBuilder description = new StringBuilder();
                for (int i = 2; i < args.length; i++) {
                    description.append(args[i]).append(" ");
                }
                player.sendMessage(ChatColor.GREEN + "Set " + attraction.getName() + "'s " + ChatColor.GREEN +
                        "description to " + ChatColor.DARK_AQUA + description.toString());

                attraction.setDescription(description.toString());

                ParkManager.getAttractionManager().saveToFile();
                break;
            }
            case "categories": {
                List<AttractionCategory> categories = new ArrayList<>();
                StringBuilder list = new StringBuilder();
                for (String s : args[2].split(",")) {
                    AttractionCategory category = AttractionCategory.fromString(s);
                    if (category == null) {
                        player.sendMessage(ChatColor.RED + "Unknown category '" + s + "'!");
                        continue;
                    }
                    categories.add(category);
                    list.append(category.getShortName()).append(",");
                }
                attraction.setCategories(categories);

                player.sendMessage(ChatColor.GREEN + "Set " + attraction.getName() + "'s " + ChatColor.GREEN +
                        "attraction categories to " + ChatColor.YELLOW + list.substring(0, list.length() - 1));

                ParkManager.getAttractionManager().saveToFile();
                break;
            }
            case "item": {
                ItemStack item = player.getItemInMainHand().clone();
                if (item == null || item.getType() == null || item.getType().equals(Material.AIR)) {
                    player.sendMessage(ChatColor.RED + "Hold the item in your hand that will represent the attraction in the menu!");
                    return;
                }
                attraction.setItem(item);

                player.sendMessage(ChatColor.GREEN + "Updated " + attraction.getName() + "'s " + ChatColor.GREEN + "item!");

                ParkManager.getAttractionManager().saveToFile();
                break;
            }
        }
    }
}
