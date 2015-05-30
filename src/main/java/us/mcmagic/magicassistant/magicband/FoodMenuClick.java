package us.mcmagic.magicassistant.magicband;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import us.mcmagic.magicassistant.MagicAssistant;
import us.mcmagic.magicassistant.handlers.FoodLocation;
import us.mcmagic.magicassistant.utils.BandUtil;
import us.mcmagic.magicassistant.handlers.InventoryType;

/**
 * Created by Marc on 12/14/14
 */
public class FoodMenuClick {

    @SuppressWarnings("deprecation")
    public static void handle(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        Player player = (Player) event.getWhoClicked();
        if (item.equals(BandUtil.getBackItem())) {
            MagicAssistant.inventoryUtil.openInventory(player, InventoryType.MAINMENU);
            return;
        }
        for (FoodLocation loc : MagicAssistant.foodLocations) {
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
