package us.mcmagic.parkmanager.show.actions;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.util.Vector;
import us.mcmagic.parkmanager.ParkManager;
import us.mcmagic.parkmanager.show.Show;
import us.mcmagic.parkmanager.show.handlers.armorstand.Movement;
import us.mcmagic.parkmanager.show.handlers.armorstand.ShowStand;
import us.mcmagic.parkmanager.show.handlers.armorstand.StandAction;

/**
 * Created by Marc on 10/11/15
 */
public class ArmorStandMove extends ShowAction {
    private ShowStand stand;
    private final Location loc;
    private double speed;

    public ArmorStandMove(Show show, long time, ShowStand stand, Location loc, double speed) {
        super(show, time);
        this.stand = stand;
        this.loc = loc;
        this.speed = speed;
    }

    @Override
    public void play() {
        if (!stand.hasSpawned()) {
            Bukkit.broadcast("ArmorStand with ID " + stand.getId() + " has not spawned", "arcade.bypass");
            return;
        }
        Location l = stand.getStand().getLocation();
        double x = ((loc.getX() - l.getX()) / (speed * 20));
        double y = ((loc.getY() - l.getY()) / (speed * 20));
        double z = ((loc.getZ() - l.getZ()) / (speed * 20));
        Vector motion = new Vector(x, y, z);
        stand.setMotion(new Movement(motion, speed));
        ParkManager.armorStandManager.addStand(stand, StandAction.MOVE);
    }
}