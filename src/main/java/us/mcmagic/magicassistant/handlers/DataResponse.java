package us.mcmagic.magicassistant.handlers;

import java.util.UUID;

/**
 * Created by Marc on 5/15/15
 */
public class DataResponse {
    private UUID uuid;
    private int coins;
    private int credits;
    private String onlineTime;

    public DataResponse(UUID uuid, int coins, int credits, String onlineTime) {
        this.uuid = uuid;
        this.coins = coins;
        this.credits = credits;
        this.onlineTime = onlineTime;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public int getCoins() {
        return coins;
    }

    public int getCredits() {
        return credits;
    }

    public String getOnlineTime() {
        return onlineTime;
    }
}
