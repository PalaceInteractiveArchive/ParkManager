package us.mcmagic.magicassistant.storage;

import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * Created by Marc on 10/11/15
 */
public class Locker {
    private UUID uuid;
    private StorageSize size;
    private ItemStack[] contents;

    public Locker(UUID uuid, StorageSize size, ItemStack[] contents) {
        this.uuid = uuid;
        this.size = size;
        this.contents = contents;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public StorageSize getSize() {
        return size;
    }

    public ItemStack[] getContents() {
        return contents;
    }
}