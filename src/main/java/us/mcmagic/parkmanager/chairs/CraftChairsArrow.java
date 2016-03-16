package us.mcmagic.parkmanager.chairs;

import net.minecraft.server.v1_8_R3.EntityArrow;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftArrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import us.mcmagic.parkmanager.ParkManager;

public class CraftChairsArrow extends CraftArrow implements Vehicle {

    private ChairManager manager = ParkManager.chairManager;

    public CraftChairsArrow(CraftServer server, EntityArrow arrow) {
        super(server, arrow);
    }

    @Override
    public void remove() {
        Entity e = getPassenger();
        if (e instanceof Player) {
            if (manager.isSitting((Player) e)) {
                return;
            }
        }
        super.remove();
    }
}