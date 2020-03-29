package network.palace.parkmanager.handlers.shop;

import lombok.Getter;
import network.palace.core.economy.currency.CurrencyType;
import org.bukkit.inventory.ItemStack;

public class ShopItem extends ShopEntry {
    @Getter private ItemStack item;

    public ShopItem(int id, ItemStack item, int cost, CurrencyType currencyType) {
        super(id, cost, currencyType);
        this.item = item;
    }
}