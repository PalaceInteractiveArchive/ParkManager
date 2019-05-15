package network.palace.parkmanager.handlers.storage;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

@Getter
@RequiredArgsConstructor
public class StorageData {
    @NonNull private final Inventory backpack;
    @NonNull @Setter private StorageSize backpackSize;
    @NonNull @Setter private String backpackHash;
    @NonNull @Setter private int dbBackpackSize;

    @NonNull private final Inventory locker;
    @NonNull @Setter private StorageSize lockerSize;
    @NonNull @Setter private String lockerHash;
    @NonNull @Setter private int dbLockerSize;

    @NonNull @Setter private ItemStack[] base;
    @NonNull @Setter private String baseHash;

    @NonNull @Setter private ItemStack[] build;
    @NonNull @Setter private String buildHash;

    @Setter private boolean needsUpdate = false;
    @Getter @Setter private long lastInventoryUpdate = System.currentTimeMillis();
}
