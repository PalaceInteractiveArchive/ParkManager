package network.palace.parkmanager.handlers.shop;

import lombok.Getter;
import network.palace.core.economy.CurrencyType;
import org.bukkit.inventory.ItemStack;

public class ShopItem extends ShopEntry {
    @Getter private ItemStack item;

    public ShopItem(ItemStack item, int cost, CurrencyType currencyType) {
        super(cost, currencyType);
        this.item = item;
    }
}