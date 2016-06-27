package us.mcmagic.parkmanager.show.handlers.armorstand;

import org.bukkit.entity.ArmorStand;
import us.mcmagic.parkmanager.show.handlers.ArmorData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marc on 10/11/15
 */
public class ShowStand {
    private String id;
    private boolean small;
    private ArmorData armorData;
    private boolean hasSpawned = false;
    private ArmorStand stand;
    private Movement motion;
    private List<Position> positions = new ArrayList<>();
    private Rotation rotation;

    public ShowStand(String id, boolean small, ArmorData armorData) {
        this.id = id;
        this.small = small;
        this.armorData = armorData;
        this.hasSpawned = false;
    }

    public String getId() {
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

    public void addPosition(Position position) {
        this.positions.add(position);
    }

    public void removePosition(Position position) {
        this.positions.remove(position);
    }

    public void setRotation(Rotation rotation) {
        this.rotation = rotation;
    }

    public Movement getMovement() {
        return motion;
    }

    public List<Position> getPositions() {
        return new ArrayList<>(positions);
    }

    public Rotation getRotation() {
        return rotation;
    }

    public ArmorData getArmorData() {
        return armorData;
    }

    public void despawn() {
        hasSpawned = false;
    }
}