package us.mcmagic.magicassistant.handlers;

import org.bukkit.inventory.ItemStack;

/**
 * Created by Marc on 12/22/14
 */
@SuppressWarnings("deprecation")
public class Attraction {
    private String displayName;
    private String warp;
    private int id;
    private byte data;

    public Attraction(String displayName, String warp, int id, byte data) {
        this.displayName = displayName;
        this.warp = warp;
        this.id = id;
        this.data = data;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getWarp() {
        return warp;
    }

    public int getId() {
        return id;
    }

    public byte getData() {
        return data;
    }

    public ItemStack getItem() {
        return new ItemStack(id, 1, data);
    }
}
