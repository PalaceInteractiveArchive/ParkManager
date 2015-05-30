package us.mcmagic.magicassistant.shop;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marc on 5/29/15
 */
public class Shop {
    private String name;
    private Location location;
    private List<ShopItem> items = new ArrayList<>();
    private ItemStack identifier;
    private String warp;

    public Shop(String name, Location location, List<ShopItem> items, ItemStack identifier, String warp) {
        this.name = name;
        this.location = location;
        this.items = items;
        this.identifier = identifier;
        this.warp = warp;
    }

    public String getName() {
        return name;
    }

    public Location getLocation() {
        return location;
    }

    public List<ShopItem> getItems() {
        return items;
    }

    public ItemStack getIdentifier() {
        return identifier;
    }

    public String getWarp() {
        return warp;
    }
}
