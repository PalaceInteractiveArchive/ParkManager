package network.palace.parkmanager.shop;

import lombok.Getter;
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

    public Shop(int id, String name, String warp, ItemStack item, List<ShopItem> items) {
        nextItemId = 0;
        this.id = id;
        this.name = name;
        this.warp = warp;
        this.item = item;
        this.items = items;
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
}
