package us.mcmagic.parkmanager.shop;

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
    private CurrencyType currencyType;

    public ShopItem(String name, ShopCategory category, int id, byte data, List<String> lore, int cost, CurrencyType currencyType) {
        this.displayName = name;
        this.category = category;
        this.id = id;
        this.data = data;
        this.lore = lore;
        this.cost = cost;
        this.currencyType = currencyType;
    }

    @SuppressWarnings("deprecation")
    public ItemStack getItem() {
        return new ItemCreator(Material.getMaterial(id), 1, data, displayName, lore);
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

    public CurrencyType getCurrencyType() {
        return currencyType;
    }
}