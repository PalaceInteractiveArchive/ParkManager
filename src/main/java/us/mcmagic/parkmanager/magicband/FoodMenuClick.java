package us.mcmagic.parkmanager.magicband;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import us.mcmagic.parkmanager.ParkManager;
import us.mcmagic.parkmanager.handlers.FoodLocation;
import us.mcmagic.parkmanager.handlers.InventoryType;
import us.mcmagic.parkmanager.utils.BandUtil;

/**
 * Created by Marc on 12/14/14
 */
public class FoodMenuClick {

    @SuppressWarnings("deprecation")
    public static void handle(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        Player player = (Player) event.getWhoClicked();
        if (item.equals(BandUtil.getBackItem())) {
            ParkManager.inventoryUtil.openInventory(player, InventoryType.MAINMENU);
            return;
        }
        for (FoodLocation loc : ParkManager.foodLocations) {
            if (item.getTypeId() == loc.getType()) {
                if (item.getData().getData() == loc.getData()) {
                    player.closeInventory();
                    player.performCommand("warp " + loc.getWarp());
                    return;
                }
            }
        }
    }
}
