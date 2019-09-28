package network.palace.parkmanager.handlers.shop;

import lombok.Getter;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.handlers.outfits.Outfit;
import network.palace.parkmanager.handlers.shop.ShopItem;
import network.palace.parkmanager.handlers.shop.ShopOutfit;
import org.bukkit.inventory.ItemStack;

import java.util.Comparator;
import java.util.List;

@Getter
public class Shop {
    private int nextItemId;
    private int id;
    private String name;
    private String warp;
    private ItemStack item;
    private List<ShopItem> items;
    private List<ShopOutfit> outfits;

    public Shop(int id, String name, String warp, ItemStack item, List<ShopItem> items, List<ShopOutfit> outfits) {
        nextItemId = 0;
        this.id = id;
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
        outfits.sort(Comparator.comparing(o -> {
            Outfit outfit = ParkManager.getWardrobeManager().getOutfit(o.getOutfitId());
            return outfit.getName();
        }));
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
}
