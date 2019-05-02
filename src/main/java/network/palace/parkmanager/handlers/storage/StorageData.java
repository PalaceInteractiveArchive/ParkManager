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
    private StorageSize backpackSize;
    private final Inventory locker;
    private StorageSize lockerSize;
    private final ItemStack[] hotbar;
    @Setter private boolean needsUpdate = false;
}
