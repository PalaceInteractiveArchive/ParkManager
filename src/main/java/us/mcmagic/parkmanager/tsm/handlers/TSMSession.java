package us.mcmagic.parkmanager.tsm.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Marc on 6/28/16
 */
public class TSMSession {
    private UUID uuid;
    private List<Hit> hits = new ArrayList<>();

    public TSMSession(UUID uuid) {
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