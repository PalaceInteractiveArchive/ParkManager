package us.mcmagic.magicassistant.magicband;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import us.mcmagic.magicassistant.MagicAssistant;
import us.mcmagic.magicassistant.handlers.InventoryType;
import us.mcmagic.magicassistant.utils.BandUtil;
import us.mcmagic.mcmagiccore.bungee.BungeeUtil;

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
            MagicAssistant.inventoryUtil.openInventory(player, InventoryType.MAINMENU);
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
                BungeeUtil.sendToServer(player, "HWS");
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
