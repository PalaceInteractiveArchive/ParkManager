package us.mcmagic.magicassistant.show;

import net.minecraft.server.v1_8_R1.EntityCreature;
import net.minecraft.server.v1_8_R1.NavigationAbstract;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftCreature;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import us.mcmagic.magicassistant.utils.AlgUtil;
import us.mcmagic.magicassistant.utils.MathUtil;

public class ShowNPC {
    private Entity _ent;
    private Location _target;
    private float _speed;

    public ShowNPC(Entity ent) {
        _ent = ent;
        _target = ent.getLocation();
    }

    public void SetTarget(Location target, float speed) {
        _target = target;
        _speed = speed;
    }

    public void Move() {
        if (_ent == null)
            return;

        if (!(_ent instanceof Creature))
            return;

        if (MathUtil.offset(_ent.getLocation(), _target) < 0.25)
            return;

        EntityCreature ec = ((CraftCreature) _ent).getHandle();

        //Path Finding
        NavigationAbstract nav = ec.getNavigation();
        if (MathUtil.offset(_ent.getLocation(), _target) > 12) {
            Location newTarget = _ent.getLocation();

            newTarget.add(AlgUtil.getTrajectory(_ent.getLocation(), _target).multiply(12));

            nav.a(newTarget.getX(), newTarget.getY(), newTarget.getZ(), _speed);
        } else {
            nav.a(_target.getX(), _target.getY(), _target.getZ(), _speed);
        }

        //FAST
        //ec.getControllerMove().a(_target.getX(), _target.getY(), _target.getZ(), _speed);
    }

    public void Clean() {
        if (_ent != null)
            _ent.remove();
    }

    public Entity GetEntity() {
        return _ent;
    }
}
