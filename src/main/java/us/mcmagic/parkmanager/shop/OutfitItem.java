package us.mcmagic.parkmanager.shop;

/**
 * Created by Marc on 11/19/15
 */
public class OutfitItem {
    private int outfitId;
    private int cost;

    public OutfitItem(int outfitId, int cost) {
        this.outfitId = outfitId;
        this.cost = cost;
    }

    public int getOutfitId() {
        return outfitId;
    }

    public int getCost() {
        return cost;
    }
}