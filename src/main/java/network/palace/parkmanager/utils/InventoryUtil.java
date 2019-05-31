package network.palace.parkmanager.utils;

import network.palace.core.player.CPlayer;
import network.palace.parkmanager.handlers.magicband.MenuType;
import network.palace.parkmanager.handlers.storage.StorageData;

public class InventoryUtil {

    public void openMenu(CPlayer player, MenuType type) {
        switch (type) {
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
