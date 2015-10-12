package us.mcmagic.magicassistant.storage;

import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * Created by Marc on 10/10/15
 */
public class Backpack {
    private UUID uuid;
    private StorageSize size;
    private ItemStack[] contents;

    public Backpack(UUID uuid, StorageSize size, ItemStack[] contents) {
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