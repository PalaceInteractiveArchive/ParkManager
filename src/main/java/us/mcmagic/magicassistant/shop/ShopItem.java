package us.mcmagic.magicassistant.shop;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import us.mcmagic.mcmagiccore.itemcreator.ItemCreator;

import java.util.List;

/**
 * Created by Marc on 5/29/15
 */
public class ShopItem {
    private String displayName;
    private ShopCategory category;
    private int id;
    private byte data;
    private final List<String> lore;
    private int cost;

    public ShopItem(String name, ShopCategory category, int id, byte data, List<String> lore, int cost) {
        this.displayName = name;
        this.category = category;
        this.id = id;
        this.data = data;
        this.lore = lore;
        this.cost = cost;
    }

    @SuppressWarnings("deprecation")
    public ItemStack getItem() {
        return new ItemCreator(Material.getMaterial(id), displayName, lore);
    }

    public String getDisplayName() {
        return displayName;
    }

    public ShopCategory getCategory() {
        return category;
    }

    public int getId() {
        return id;
    }

    public byte getData() {
        return data;
    }

    public List<String> getLore() {
        return lore;
    }

    public int getCost() {
        return cost;
    }
}
