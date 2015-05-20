package us.mcmagic.magicassistant.show;

import net.minecraft.server.v1_8_R2.EntityCreature;
import net.minecraft.server.v1_8_R2.NavigationAbstract;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R2.entity.CraftCreature;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import us.mcmagic.magicassistant.utils.AlgUtil;
import us.mcmagic.magicassistant.utils.MathUtil;

public class ShowNPC {
    private Entity entity;
    private Location loc;
    private float speed;

    public ShowNPC(Entity ent) {
        entity = ent;
        loc = ent.getLocation();
    }

    public void SetTarget(Location target, float speed) {
        loc = target;
        this.speed = speed;
    }

    public void move() {
        if (entity == null)
            return;
        if (!(entity instanceof Creature))
            return;
        if (MathUtil.offset(entity.getLocation(), loc) < 0.25)
            return;
        EntityCreature ec = ((CraftCreature) entity).getHandle();
        //Path Finding
        NavigationAbstract nav = ec.getNavigation();
        if (MathUtil.offset(entity.getLocation(), loc) > 12) {
            Location newTarget = entity.getLocation();
            newTarget.add(AlgUtil.getTrajectory(entity.getLocation(), loc).multiply(12));
            nav.a(newTarget.getX(), newTarget.getY(), newTarget.getZ(), speed);
        } else {
            nav.a(loc.getX(), loc.getY(), loc.getZ(), speed);
        }
        //FAST
        //ec.getControllerMove().a(loc.getX(), loc.getY(), loc.getZ(), speed);
    }

    public void clean() {
        if (entity != null)
            entity.remove();
    }

    public Entity getEntity() {
        return entity;
    }
}
