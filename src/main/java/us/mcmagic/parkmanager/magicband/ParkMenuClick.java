package us.mcmagic.parkmanager.magicband;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import us.mcmagic.mcmagiccore.bungee.BungeeUtil;
import us.mcmagic.parkmanager.ParkManager;
import us.mcmagic.parkmanager.handlers.InventoryType;
import us.mcmagic.parkmanager.utils.BandUtil;

/**
 * Created by Marc on 12/15/14
 */
public class ParkMenuClick {

    public static void handle(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        if (item == null) {
            return;
        }
        if (item.getItemMeta() == null) {
            return;
        }
        Player player = (Player) event.getWhoClicked();
        if (item.equals(BandUtil.getBackItem())) {
            ParkManager.inventoryUtil.openInventory(player, InventoryType.MAINMENU);
            return;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta.getDisplayName() == null) {
            return;
        }
        String name = ChatColor.stripColor(meta.getDisplayName());
        switch (name) {
            case "Transportation and Ticket Center":
                player.closeInventory();
                player.sendMessage(ChatColor.GREEN + "Now joining " + ChatColor.AQUA + "" + ChatColor.BOLD + "TTC...");
                BungeeUtil.sendToServer(player, "TTC");
                return;
            case "Creative":
                player.closeInventory();
                player.sendMessage(ChatColor.GREEN + "Now joining " + ChatColor.AQUA + "" + ChatColor.BOLD + "Creative...");
                BungeeUtil.sendToServer(player, "Creative");
                return;
            case "Arcade":
                player.closeInventory();
                player.sendMessage(ChatColor.GREEN + "Now joining " + ChatColor.AQUA + "" + ChatColor.BOLD + "Arcade...");
                BungeeUtil.sendToServer(player, "Arcade");
                return;
            case "Magic Kingdom":
                player.closeInventory();
                player.sendMessage(ChatColor.GREEN + "Now joining " + ChatColor.AQUA + "" + ChatColor.BOLD + "Magic Kingdom...");
                BungeeUtil.sendToServer(player, "MK");
                return;
            case "Epcot":
                player.closeInventory();
                player.sendMessage(ChatColor.GREEN + "Now joining " + ChatColor.AQUA + "" + ChatColor.BOLD + "Epcot...");
                BungeeUtil.sendToServer(player, "Epcot");
                return;
            case "Hollywood Studios":
                player.closeInventory();
                player.sendMessage(ChatColor.GREEN + "Now joining " + ChatColor.AQUA + "" + ChatColor.BOLD + "Hollywood Studios...");
                BungeeUtil.sendToServer(player, "DHS");
                return;
            case "Animal Kingdom":
                player.closeInventory();
                player.sendMessage(ChatColor.GREEN + "Now joining " + ChatColor.AQUA + "" + ChatColor.BOLD + "Animal Kingdom...");
                BungeeUtil.sendToServer(player, "AK");
                return;
            case "Typhoon Lagoon":
                player.closeInventory();
                player.sendMessage(ChatColor.GREEN + "Now joining " + ChatColor.AQUA + "" + ChatColor.BOLD + "Typhoon Lagoon...");
                BungeeUtil.sendToServer(player, "Typhoon");
                return;
            case "Disney Cruise Line":
                player.closeInventory();
                player.sendMessage(ChatColor.GREEN + "Now joining " + ChatColor.AQUA + "" + ChatColor.BOLD + "Disney Cruise Line...");
                BungeeUtil.sendToServer(player, "DCL");
                return;
            case "Seasonal":
                player.closeInventory();
                player.sendMessage(ChatColor.GREEN + "Now joining " + ChatColor.AQUA + "" + ChatColor.BOLD + "Seasonal...");
                BungeeUtil.sendToServer(player, "Seasonal");
                return;
            default:
                player.closeInventory();
                player.sendMessage(ChatColor.RED + "There was an error, please report this to a Staff Member!");
        }
    }
}
