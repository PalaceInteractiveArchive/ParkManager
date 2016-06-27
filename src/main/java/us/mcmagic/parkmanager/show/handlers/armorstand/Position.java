package us.mcmagic.parkmanager.show.handlers.armorstand;

import org.bukkit.util.Vector;

/**
 * Created by Marc on 10/11/15
 */
public class Position {
    private Vector motion;
    private long duration;
    private final PositionType type;

    public Position(Vector motion, double speed, PositionType type) {
        this.motion = motion;
        this.duration = (long) speed;
        this.type = type;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public Vector getMotion() {
        return motion;
    }

    public void setMotion(Vector motion) {
        this.motion = motion;
    }

    public PositionType getPositionType() {
        return type;
    }
}