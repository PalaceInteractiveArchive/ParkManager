package network.palace.parkmanager.handlers.storage;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import network.palace.core.utils.ItemUtil;
import network.palace.parkmanager.handlers.Resort;
import network.palace.parkmanager.storage.StorageManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResortInventory {
    private Resort resort;
    private String backpackJSON = "";
    private String backpackHash = "";
    private String dbBackpackHash = "";
    private int backpackSize;

    private String lockerJSON = "";
    private String lockerHash = "";
    private String dbLockerHash = "";
    private int lockerSize;

    private String baseJSON = "";
    private String baseHash = "";
    private String dbBaseHash = "";

    private String buildJSON = "";
    private String buildHash = "";
    private String dbBuildHash = "";

    /**
     * Check if all JSON entries are empty (meaning no data is here, not even empty inventories)
     *
     * @return true if four JSON entries are empty, otherwise false
     */
    public boolean isEmpty() {
        return backpackJSON.isEmpty() && lockerJSON.isEmpty() && baseJSON.isEmpty() && buildJSON.isEmpty();
    }

    public StorageData toStorageData() {
        ItemStack[] backpackArray = ItemUtil.getInventoryFromJsonNew(getBackpackJSON());
        ItemStack[] lockerArray = ItemUtil.getInventoryFromJsonNew(getLockerJSON());
        ItemStack[] baseArray = ItemUtil.getInventoryFromJsonNew(getBaseJSON());
        ItemStack[] buildArray = ItemUtil.getInventoryFromJsonNew(getBuildJSON());

        StorageSize backpackSize = StorageSize.fromInt(getBackpackSize());
        StorageSize lockerSize = StorageSize.fromInt(getLockerSize());

        StorageManager.filterItems(backpackArray);
        StorageManager.filterItems(lockerArray);
        StorageManager.filterItems(baseArray);

        Inventory backpack = Bukkit.createInventory(null, backpackSize.getSlots(), ChatColor.BLUE + "Your Backpack");
        Inventory locker = Bukkit.createInventory(null, lockerSize.getSlots(), ChatColor.BLUE + "Your Locker");

        StorageManager.fillInventory(backpack, backpackSize, backpackArray);
        StorageManager.fillInventory(locker, lockerSize, lockerArray);

        return new StorageData(
                backpack, backpackSize, getBackpackHash(), getBackpackSize(),
                locker, lockerSize, getLockerHash(), getLockerSize(),
                baseArray, getBaseHash(), buildArray, getBuildHash()
        );
    }
}