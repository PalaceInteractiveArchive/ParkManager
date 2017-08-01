package network.palace.parkmanager.handlers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

@Getter
@Setter
@AllArgsConstructor
public class Warp {
    public String name;
    public String server;
    public double x;
    public double y;
    public double z;
    public float yaw;
    public float pitch;
    public String world;

    public World getWorld() {
        if (Bukkit.getWorlds().get(0).getName().equals(world)) {
            return Bukkit.getWorld(world);
        }
        return null;
    }

    public Location getLocation() {
        return new Location(getWorld(), x, y, z, yaw, pitch);
    }

    public static Warp fromDatabaseString(String warpString) {
        if (warpString == null || warpString.equals(""))
            return null;
        String[] tokens = warpString.split(":");
        return new Warp(tokens[0], tokens[1], Double.parseDouble(tokens[2]),
                Double.parseDouble(tokens[3]), Double.parseDouble(tokens[4]),
                Float.parseFloat(tokens[5]), Float.parseFloat(tokens[6]), tokens[7]);
    }

    public String toDatabaseString() {
        return name + ":" + server + ":" + Double.toString(x) + ":" + Double.toString(y) + ":" +
                Double.toString(z) + ":" + Float.toString(yaw) + ":" + Float.toString(pitch) + ":" +
                world;
    }

}