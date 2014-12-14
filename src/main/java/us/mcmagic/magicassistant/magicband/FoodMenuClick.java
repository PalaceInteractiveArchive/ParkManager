package us.mcmagic.magicassistant.magicband;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import us.mcmagic.magicassistant.FoodLocation;
import us.mcmagic.magicassistant.MagicAssistant;

/**
 * Created by Marc on 12/14/14
 */
public class FoodMenuClick {

    public static void handle(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        Player player = (Player) event.getWhoClicked();
        for (FoodLocation loc : MagicAssistant.foodLocations) {
            if (item.getTypeId() == loc.getType()
                    && item.getData().getData() == loc.getData()) {
                player.closeInventory();
                player.performCommand("warp " + loc.getWarp());
                return;
            }
            return;
        }
    }
}
