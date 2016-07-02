package us.mcmagic.parkmanager.tsm.handlers;

import java.util.UUID;

/**
 * Created by Marc on 6/28/16
 */
public class Hit {
    private UUID uuid;
    private long time = System.currentTimeMillis();
    private int points;

    public Hit(UUID uuid, int points) {
        this.uuid = uuid;
        this.points = points;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public long getTime() {
        return time;
    }

    public int getPoints() {
        return points;
    }
}
