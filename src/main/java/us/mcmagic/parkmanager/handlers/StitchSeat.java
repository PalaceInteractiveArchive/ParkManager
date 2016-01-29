package us.mcmagic.parkmanager.handlers;

import org.bukkit.Location;

import java.util.UUID;

/**
 * Created by Marc on 1/10/15
 */
public class StitchSeat {
    private int id;
    private Location location;
    private UUID occupant = null;

    public StitchSeat(int id, Location location) {
        this.id = id;
        this.location = location;
    }

    public boolean inUse() {
        return occupant != null;
    }

    public int getId() {
        return id;
    }

    public Location getLocation() {
        return location.clone();
    }

    public UUID getOccupant() {
        return occupant;
    }

    public void setOccupant(UUID uuid) {
        this.occupant = uuid;
    }

    public void clearOccupant() {
        this.occupant = null;
    }
}
