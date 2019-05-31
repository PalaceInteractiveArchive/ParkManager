package network.palace.parkmanager.shop;

import lombok.Getter;
import lombok.Setter;
import network.palace.core.economy.CurrencyType;
import org.bukkit.inventory.ItemStack;

@Getter
public class ShopItem {
    @Setter private int id;
    private ItemStack item;
    private int cost;
    private CurrencyType currencyType;

    public ShopItem(ItemStack item, int cost, CurrencyType currencyType) {
        this.item = item;
        this.cost = cost;
        this.currencyType = currencyType;
    }
}