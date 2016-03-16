package us.mcmagic.parkmanager.chairs;

import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.EntityArrow;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;

public class NMSChairsArrow extends EntityArrow {

    public NMSChairsArrow(CraftWorld world, Location location) {
        super(world.getHandle());
        setPositionRotation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        this.world.addEntity(this);
        this.bukkitEntity = new CraftChairsArrow((CraftServer) Bukkit.getServer(), this);
        this.setYawPitch(0, 45);
    }

    @Override
    public void g(double x, double y, double z) {
    }

    @Override
    public void collide(Entity entity) {
    }
}