package network.palace.parkmanager.handlers.shop;

import lombok.Getter;
import lombok.Setter;
import network.palace.core.economy.CurrencyType;

@Getter
public abstract class ShopEntry {
    @Setter private int id;
    private int cost;
    private CurrencyType currencyType;

    public ShopEntry(int cost, CurrencyType currencyType) {
        this.cost = cost;
        this.currencyType = currencyType;
    }
}
