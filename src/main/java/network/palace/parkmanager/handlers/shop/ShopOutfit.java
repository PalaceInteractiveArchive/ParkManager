package network.palace.parkmanager.handlers.shop;

import lombok.Getter;
import network.palace.core.economy.currency.CurrencyType;

public class ShopOutfit extends ShopEntry {
    @Getter private int outfitId;

    public ShopOutfit(int id, int outfitId, int cost) {
        super(id, cost, CurrencyType.TOKENS);
        this.outfitId = outfitId;
    }
}