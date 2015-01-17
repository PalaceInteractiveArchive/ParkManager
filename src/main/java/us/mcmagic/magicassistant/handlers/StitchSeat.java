package us.mcmagic.magicassistant.handlers;

import org.bukkit.Location;

import java.util.UUID;

/**
 * Created by Marc on 1/10/15
 */
public class StitchSeat {
    public int id;
    public Location loc;
    public UUID occupant;

    public StitchSeat(int id, Location loc) {
        this.id = id;
        this.loc = loc;
    }

    public int getId() {
        return id;
    }

    public Location getLocation() {
        return loc;
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
