package us.mcmagic.parkmanager.handlers;

import java.util.UUID;

/**
 * Created by Marc on 5/15/15
 */
public class DataResponse {
    private UUID uuid;
    private int balance;
    private int tokens;
    private String onlineTime;

    public DataResponse(UUID uuid, int balance, int tokens, String onlineTime) {
        this.uuid = uuid;
        this.balance = balance;
        this.tokens = tokens;
        this.onlineTime = onlineTime;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public int getBalance() {
        return balance;
    }

    public int getTokens() {
        return tokens;
    }

    public String getOnlineTime() {
        return onlineTime;
    }
}
