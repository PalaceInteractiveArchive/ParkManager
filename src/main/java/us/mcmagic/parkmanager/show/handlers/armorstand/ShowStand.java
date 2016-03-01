package us.mcmagic.parkmanager.show.handlers.armorstand;

import org.bukkit.entity.ArmorStand;
import us.mcmagic.parkmanager.show.handlers.ArmorData;

/**
 * Created by Marc on 10/11/15
 */
public class ShowStand {
    private int id;
    private boolean small;
    private ArmorData armorData;
    private boolean hasSpawned = false;
    private ArmorStand stand;
    private Movement motion;
    private Position position;

    public ShowStand(int id, boolean small, ArmorData armorData) {
        this.id = id;
        this.small = small;
        this.armorData = armorData;
    }

    public int getId() {
        return id;
    }

    public boolean isSmall() {
        return small;
    }

    public boolean hasSpawned() {
        return hasSpawned;
    }

    public void spawn() {
        hasSpawned = true;
    }

    public ArmorStand getStand() {
        return stand;
    }

    public void setStand(ArmorStand stand) {
        this.stand = stand;
    }

    public void setMotion(Movement motion) {
        this.motion = motion;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public Movement getMovement() {
        return motion;
    }

    public Position getPosition() {
        return position;
    }

    public ArmorData getArmorData() {
        return armorData;
    }

    public void despawn() {
        hasSpawned = false;
    }
}