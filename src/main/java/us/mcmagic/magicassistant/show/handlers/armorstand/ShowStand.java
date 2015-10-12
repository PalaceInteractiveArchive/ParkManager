package us.mcmagic.magicassistant.show.handlers.armorstand;

import org.bukkit.entity.ArmorStand;

/**
 * Created by Marc on 10/11/15
 */
public class ShowStand {
    private int id;
    private boolean small;
    private boolean hasSpawned = false;
    private ArmorStand stand;
    private Movement motion;

    public ShowStand(int id, boolean small) {
        this.id = id;
        this.small = small;
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

    public Movement getMovement() {
        return motion;
    }

    public void despawn() {
        hasSpawned = false;
    }
}