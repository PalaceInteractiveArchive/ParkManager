package network.palace.parkmanager.magicband;

import network.palace.core.Core;
import network.palace.core.dashboard.packets.dashboard.PacketSendToServer;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.handlers.InventoryType;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import network.palace.parkmanager.utils.BandUtil;

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
                Core.getDashboardConnection().send(new PacketSendToServer(player.getUniqueId(), "TTC"));
                return;
            case "Creative":
                player.closeInventory();
                player.sendMessage(ChatColor.GREEN + "Now joining " + ChatColor.AQUA + "" + ChatColor.BOLD + "Creative...");
                Core.getDashboardConnection().send(new PacketSendToServer(player.getUniqueId(), "Creative"));
                return;
            case "Arcade":
                player.closeInventory();
                player.sendMessage(ChatColor.GREEN + "Now joining " + ChatColor.AQUA + "" + ChatColor.BOLD + "Arcade...");
                Core.getDashboardConnection().send(new PacketSendToServer(player.getUniqueId(), "Arcade"));
                return;
            case "Magic Kingdom":
                player.closeInventory();
                player.sendMessage(ChatColor.GREEN + "Now joining " + ChatColor.AQUA + "" + ChatColor.BOLD + "Magic Kingdom...");
                Core.getDashboardConnection().send(new PacketSendToServer(player.getUniqueId(), "MK"));
                return;
            case "Epcot":
                player.closeInventory();
                player.sendMessage(ChatColor.GREEN + "Now joining " + ChatColor.AQUA + "" + ChatColor.BOLD + "Epcot...");
                Core.getDashboardConnection().send(new PacketSendToServer(player.getUniqueId(), "Epcot"));
                return;
            case "Hollywood Studios":
                player.closeInventory();
                player.sendMessage(ChatColor.GREEN + "Now joining " + ChatColor.AQUA + "" + ChatColor.BOLD + "Hollywood Studios...");
                Core.getDashboardConnection().send(new PacketSendToServer(player.getUniqueId(), "DHS"));
                return;
            case "Animal Kingdom":
                player.closeInventory();
                player.sendMessage(ChatColor.GREEN + "Now joining " + ChatColor.AQUA + "" + ChatColor.BOLD + "Animal Kingdom...");
                Core.getDashboardConnection().send(new PacketSendToServer(player.getUniqueId(), "AK"));
                return;
            case "Typhoon Lagoon":
                player.closeInventory();
                player.sendMessage(ChatColor.GREEN + "Now joining " + ChatColor.AQUA + "" + ChatColor.BOLD + "Typhoon Lagoon...");
                Core.getDashboardConnection().send(new PacketSendToServer(player.getUniqueId(), "Typhoon"));
                return;
            case "Disney Cruise Line":
                player.closeInventory();
                player.sendMessage(ChatColor.GREEN + "Now joining " + ChatColor.AQUA + "" + ChatColor.BOLD + "Disney Cruise Line...");
                Core.getDashboardConnection().send(new PacketSendToServer(player.getUniqueId(), "DCL"));
                return;
            case "Seasonal":
                player.closeInventory();
                player.sendMessage(ChatColor.GREEN + "Now joining " + ChatColor.AQUA + "" + ChatColor.BOLD + "Seasonal...");
                Core.getDashboardConnection().send(new PacketSendToServer(player.getUniqueId(), "Seasonal"));
                return;
            default:
                player.closeInventory();
                player.sendMessage(ChatColor.RED + "There was an error, please report this to a Staff Member!");
        }
    }
}
