package us.mcmagic.magicassistant.backpack;

import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * Created by Marc on 10/10/15
 */
public class Backpack {
    private UUID uuid;
    private BackpackSize size;
    private ItemStack[] contents;

    public Backpack(UUID uuid, BackpackSize size, ItemStack[] contents) {
        this.uuid = uuid;
        this.size = size;
        this.contents = contents;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public BackpackSize getSize() {
        return size;
    }

    public ItemStack[] getContents() {
        return contents;
    }
}