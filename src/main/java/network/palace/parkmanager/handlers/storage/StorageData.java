package network.palace.parkmanager.handlers.storage;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

@Getter
@RequiredArgsConstructor
public class StorageData {
    private final Inventory backpack;
    @Setter private StorageSize backpackSize;
    @Setter private String backpackHash = "";
    private final Inventory locker;
    @Setter private StorageSize lockerSize;
    @Setter private String lockerHash = "";
    private final ItemStack[] hotbar;
    @Setter private String hotbarHash = "";
    @Setter private boolean needsUpdate = false;
    @Getter @Setter private long lastInventoryUpdate = System.currentTimeMillis();
}
