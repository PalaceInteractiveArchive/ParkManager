package network.palace.parkmanager.handlers.shop;

import lombok.Getter;
import network.palace.core.economy.CurrencyType;

public class ShopOutfit extends ShopEntry {
    @Getter private int outfitId;

    public ShopOutfit(int outfitId, int cost) {
        super(cost, CurrencyType.TOKENS);
        this.outfitId = outfitId;
    }
}