package us.mcmagic.parkmanager.bb8;

import org.bukkit.Location;
import org.bukkit.util.Vector;

// Author: BeMacized
// http://models.bemacized.net/

public class PosRotUtils {
    public static float getYaw(Location l1, Location l2) {
        return getLookAtYaw(new Vector(l2.getX(), l2.getY(), l2.getZ()).subtract(new Vector(l1.getX(), l1.getY(), l1.getZ())));
    }

    public static float getLookAtYaw(Vector vector) {
        double dx = vector.getX();
        double dz = vector.getZ();
        double yaw = (dx != 0) ? ((dx < 0) ? 1.5 * Math.PI : 0.5 * Math.PI) - Math.atan(dz / dx) : ((dz < 0) ? Math.PI : 0);
        return (float) (-yaw * 180 / Math.PI - 180);
    }

    public static Location closestGround(Location loc) {
        Location up = loc.clone();
        Location down = loc.clone();
        while (!((up.getBlock().isEmpty() && up.getBlock().getRelative(0,1,0).isEmpty() && !up.getBlock().getRelative(0,-1,0).isEmpty()&& !up.getBlock().getRelative(0,-1,0).isLiquid()) ||
                (down.getBlock().isEmpty() && down.getBlock().getRelative(0,1,0).isEmpty() && !down.getBlock().getRelative(0,-1,0).isEmpty()&& !down.getBlock().getRelative(0,-1,0).isLiquid()))) {
            if (up.getY() <= 253) up = up.clone().add(0,1,0);
            if (down.getY() >= 2) down = down.clone().add(0,-1,0);
            if (up.getY() > 253 && down.getY() < 2) break;
        }
        if ((up.getBlock().isEmpty() && up.getBlock().getRelative(0,1,0).isEmpty() && !up.getBlock().getRelative(0,-1,0).isEmpty()&& !up.getBlock().getRelative(0,-1,0).isLiquid())) {
            return up;
        }
        if ((down.getBlock().isEmpty() && down.getBlock().getRelative(0,1,0).isEmpty() && !down.getBlock().getRelative(0,-1,0).isEmpty()&& !down.getBlock().getRelative(0,-1,0).isLiquid())) {
            return down;
        }
        return null;
    }
}