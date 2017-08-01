package network.palace.parkmanager.handlers;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

/**
 * Created by Marc on 5/15/15
 */
@Getter
@AllArgsConstructor
public class DataResponse {
    private UUID uuid;
    private int balance;
    private int tokens;

    public UUID getUniqueId() {
        return uuid;
    }
}
