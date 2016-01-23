package us.mcmagic.parkmanager.magicband;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import us.mcmagic.parkmanager.ParkManager;
import us.mcmagic.parkmanager.handlers.InventoryType;
import us.mcmagic.parkmanager.handlers.Ride;
import us.mcmagic.parkmanager.utils.BandUtil;

/**
 * Created by Marc on 12/22/14
 */
public class RideListClick {

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
            ParkManager.inventoryUtil.openInventory(player, InventoryType.RIDESANDATTRACTIONS);
            return;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta.getDisplayName() == null) {
            return;
        }
        String name = ChatColor.stripColor(meta.getDisplayName());
        if (meta.getDisplayName().equals(ChatColor.RED + "Uh oh!")) {
            player.closeInventory();
            player.sendMessage(ChatColor.RED + "Sorry, but there are no rides setup on this server!");
            return;
        }
        String invName = ChatColor.stripColor(event.getInventory().getName());
        switch (name) {
            case "Next Page":
                ParkManager.inventoryUtil.openRideListPage(player,
                        Integer.parseInt(invName.replaceAll("Ride List Page ", "")) + 1);
                return;
            case "Last Page":
                ParkManager.inventoryUtil.openRideListPage(player,
                        Integer.parseInt(invName.replaceAll("Ride List Page ", "")) - 1);
                return;
        }
        Ride ride = ParkManager.getRide(name);
        if (ride == null) {
            player.closeInventory();
            player.sendMessage(ChatColor.RED + "There was an error, please tell a Staff Member!");
            return;
        }
        player.closeInventory();
        player.performCommand("warp " + ride.getWarp());
    }
}
