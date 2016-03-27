package us.mcmagic.parkmanager.magicband;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import us.mcmagic.parkmanager.ParkManager;
import us.mcmagic.parkmanager.handlers.InventoryType;
import us.mcmagic.parkmanager.utils.BandUtil;

/**
 * Created by Marc on 12/22/14
 */
public class RideAttractionClick {

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
        if (meta.getDisplayName().equals(ChatColor.RED + "Uh oh!")) {
            player.closeInventory();
            player.sendMessage(ChatColor.RED + "Sorry, but there are no rides setup on this server!");
            return;
        }
        String name = ChatColor.stripColor(meta.getDisplayName());
        switch (name) {
            case "Wait Times":
                ParkManager.inventoryUtil.openWaitTimes(player);
                return;
            case "Rides":
                ParkManager.inventoryUtil.openRideList(player);
                return;
            case "Meet & Greets":
                ParkManager.inventoryUtil.openMeetAndGreetList(player);
                return;
            case "Attractions":
                ParkManager.inventoryUtil.openAttractionList(player);
        }
    }
}