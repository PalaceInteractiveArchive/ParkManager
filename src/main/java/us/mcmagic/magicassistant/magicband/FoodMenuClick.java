package us.mcmagic.magicassistant.magicband;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import us.mcmagic.magicassistant.FoodLocation;
import us.mcmagic.magicassistant.MagicAssistant;
import us.mcmagic.magicassistant.utils.BandUtil;
import us.mcmagic.magicassistant.utils.InventoryType;
import us.mcmagic.magicassistant.utils.InventoryUtil;

/**
 * Created by Marc on 12/14/14
 */
public class FoodMenuClick {

    @SuppressWarnings("deprecation")
    public static void handle(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        Player player = (Player) event.getWhoClicked();
        if (item.equals(BandUtil.getBackItem())) {
            InventoryUtil.openInventory(player, InventoryType.MAINMENU);
            return;
        }
        player.sendMessage("Item: " + item.getTypeId() + ":" + item.getData().getData());
        for (FoodLocation loc : MagicAssistant.foodLocations) {
            player.sendMessage("Location: " + loc.getType() + ":" + loc.getData());
            if (item.getTypeId() == loc.getType()) {
                if (loc.getData() == 0) {
                    player.closeInventory();
                    player.performCommand("warp " + loc.getWarp());
                    return;
                } else if (item.getData().getData() == loc.getData()) {
                    player.closeInventory();
                    player.performCommand("warp " + loc.getWarp());
                    return;
                }
            }
            return;
        }
    }
}
