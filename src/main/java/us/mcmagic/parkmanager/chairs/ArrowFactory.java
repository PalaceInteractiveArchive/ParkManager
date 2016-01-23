package us.mcmagic.parkmanager.chairs;

import net.minecraft.server.v1_8_R3.EntityArrow;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Arrow;

public class ArrowFactory implements IArrowFactory {

    public Arrow spawnArrow(Location location) {
        CraftWorld world = (CraftWorld) location.getWorld();
        EntityArrow arrow = new NMSChairsArrow(world, location);
        return (Arrow) arrow.getBukkitEntity();
    }

}