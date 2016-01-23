package us.mcmagic.parkmanager.handlers;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class Warp {
    public String name;
    public String server;
    public double x;
    public double y;
    public double z;
    public float yaw;
    public float pitch;
    public String world;

    public Warp(String name, String server, double x, double y, double z,
                float yaw, float pitch, String world) {
        this.name = name;
        this.server = server;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.world = world;
    }

    public String getName() {
        return name;
    }

    public String getServer() {
        return server;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public World getWorld() {
        if (Bukkit.getWorlds().get(0).getName().equals(world)) {
            return Bukkit.getWorld(world);
        }
        return null;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public void setWorld(String world) {
        this.world = world;
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