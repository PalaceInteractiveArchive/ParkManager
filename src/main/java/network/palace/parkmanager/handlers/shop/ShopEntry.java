package network.palace.parkmanager.handlers.shop;

import lombok.Getter;
import network.palace.core.economy.CurrencyType;

@Getter
public abstract class ShopEntry {
    private int id;
    private int cost;
    private CurrencyType currencyType;

    public ShopEntry(int id, int cost, CurrencyType currencyType) {
        this.id = id;
        this.cost = cost;
        this.currencyType = currencyType;
    }
}
