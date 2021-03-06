package network.palace.parkmanager.handlers.shop;

import lombok.Getter;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.handlers.ParkType;
import network.palace.parkmanager.handlers.outfits.Outfit;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

public class Shop {
    private int nextItemId;
    @Getter private String id;
    @Getter private ParkType park;
    @Getter private String name;
    @Getter private String warp;
    @Getter private ItemStack item;
    @Getter private List<ShopItem> items;
    @Getter private List<ShopOutfit> outfits;

    public Shop(String id, ParkType park, String name, String warp, ItemStack item, List<ShopItem> items, List<ShopOutfit> outfits) {
        nextItemId = items.size();
        this.id = id;
        this.park = park;
        this.name = name;
        this.warp = warp;
        this.item = item;
        this.items = items;
        this.outfits = outfits;
        sortItems();
    }

    public void addItem(ShopItem item) {
        items.add(item);
        sortItems();
    }

    private void sortItems() {
        items.sort(Comparator.comparing(o -> o.getItem().getItemMeta().getDisplayName()));
    }

    public ShopItem getItem(int id) {
        for (ShopItem item : items) {
            if (item.getId() == id) {
                return item;
            }
        }
        return null;
    }

    public void removeItem(int id) {
        ShopItem item = getItem(id);
        if (item != null) items.remove(item);
    }

    public void addOutfit(ShopOutfit outfit) {
        outfits.add(outfit);
        sortOutfits();
    }

    private void sortOutfits() {
        List<Integer> toRemove = new ArrayList<>();
        outfits.sort(Comparator.comparing(o -> {
            Outfit outfit = ParkManager.getWardrobeManager().getOutfit(o.getOutfitId());
            if (outfit != null) return outfit.getName();
            toRemove.add(o.getOutfitId());
            return null;
        }));
        outfits.removeIf(shopOutfit -> toRemove.contains(shopOutfit.getOutfitId()));
    }

    public ShopOutfit getOutfit(int id) {
        for (ShopOutfit outfit : outfits) {
            if (outfit.getId() == id) {
                return outfit;
            }
        }
        return null;
    }

    public void removeOutfit(int id) {
        ShopOutfit outfit = getOutfit(id);
        if (outfit != null) outfits.remove(outfit);
    }

    public int nextId() {
        return nextItemId++;
    }
}
