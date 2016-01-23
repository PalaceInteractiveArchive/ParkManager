package us.mcmagic.parkmanager.shop;

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
    private List<OutfitItem> outfits = new ArrayList<>();
    private ItemStack identifier;
    private String warp;
    private double radius;

    public Shop(String name, Location location, List<ShopItem> items, List<OutfitItem> outfits, ItemStack identifier,
                String warp, double radius) {
        this.name = name;
        this.location = location;
        this.items = items;
        this.outfits = outfits;
        this.identifier = identifier;
        this.warp = warp;
        this.radius = radius;
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

    public List<OutfitItem> getOutfits() {
        return outfits;
    }

    public ItemStack getIdentifier() {
        return identifier;
    }

    public String getWarp() {
        return warp;
    }

    public double getRadius() {
        return radius;
    }
}
