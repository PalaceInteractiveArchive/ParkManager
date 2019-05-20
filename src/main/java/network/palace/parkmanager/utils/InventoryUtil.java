package network.palace.parkmanager.utils;

import com.google.common.collect.ImmutableMap;
import network.palace.core.menu.Menu;
import network.palace.core.menu.MenuButton;
import network.palace.core.player.CPlayer;
import network.palace.core.utils.ItemUtil;
import network.palace.parkmanager.handlers.magicband.MenuType;
import network.palace.parkmanager.handlers.storage.StorageData;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.List;

public class InventoryUtil {

    public void openMenu(CPlayer player, MenuType type) {
        switch (type) {
            case BAND_MAIN:
                List<MenuButton> buttons = new ArrayList<>();
                buttons.add(new MenuButton(16, ItemUtil.create(Material.GREEN_WOOL, "Player Visibility"), ImmutableMap.of(ClickType.LEFT, p -> {

                })));
                Menu menu = new Menu(null, ChatColor.BLUE + "MagicBand", player, buttons);
                break;
            case WATCH:
                break;
            case BACKPACK: {
                if (!player.getRegistry().hasEntry("storageData")) return;
                StorageData data = (StorageData) player.getRegistry().getEntry("storageData");
                player.openInventory(data.getBackpack());
                break;
            }
            case LOCKER: {
                if (!player.getRegistry().hasEntry("storageData")) return;
                StorageData data = (StorageData) player.getRegistry().getEntry("storageData");
                player.openInventory(data.getLocker());
                break;
            }
        }
    }

    /**
     * Determine whether the slot provided is reserved for static items in 'guest' mode
     *
     * @param slot the slot
     * @return true if slot is reserved for static items in 'guest' mode
     */
    public static boolean isReservedSlot(int slot) {
        return slot >= 5 && slot <= 8;
    }
}
