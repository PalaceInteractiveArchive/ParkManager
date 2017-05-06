package network.palace.parkmanager.handlers;

import java.util.UUID;

/**
 * Created by Marc on 5/15/15
 */
public class DataResponse {
    private UUID uuid;
    private int balance;
    private int tokens;

    public DataResponse(UUID uuid, int balance, int tokens) {
        this.uuid = uuid;
        this.balance = balance;
        this.tokens = tokens;
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
}
