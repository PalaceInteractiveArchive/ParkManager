package network.palace.parkmanager.tsm.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Marc on 6/28/16
 */
public class ShooterSession {
    private UUID uuid;
    private List<Hit> hits = new ArrayList<>();

    public ShooterSession(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public List<Hit> getHits() {
        return hits;
    }

    public void addHit(Hit hit) {
        hits.add(hit);
    }
}