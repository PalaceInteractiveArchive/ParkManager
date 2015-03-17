package us.mcmagic.magicassistant.show.actions;

import org.bukkit.Bukkit;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.Firework;
import org.bukkit.event.Listener;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.util.Vector;
import us.mcmagic.magicassistant.show.Show;

import java.lang.reflect.Method;
import java.util.ArrayList;

public class FireworkAction extends ShowAction implements Listener {
    public Location Location;
    public ArrayList<FireworkEffect> Effects;
    public int Power;
    public Vector Direction;
    public double DirectionPower;

    public FireworkAction(Show show, long time, Location loc,
                          ArrayList<FireworkEffect> effectList, int power, Vector dir,
                          double dirPow) {
        super(show, time);

        Location = loc;
        Effects = effectList;
        Power = power;
        Direction = dir;
        DirectionPower = dirPow;
    }

    @Override
    public void Play() {
        try {
            playFirework();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Method world_getHandle = null;
    private Method nms_world_broadcastEntityEffect = null;
    private Method firework_getHandle = null;

    public void playFirework() throws Exception {
        final Firework fw = Location.getWorld().spawn(Location,
                Firework.class);

        Object nms_world = null;
        Object nms_firework = null;

        if (world_getHandle == null) {
            world_getHandle = getMethod(Location.getWorld().getClass(),
                    "getHandle");
            firework_getHandle = getMethod(fw.getClass(), "getHandle");
        }

        nms_world = world_getHandle
                .invoke(Location.getWorld(), (Object[]) null);
        nms_firework = firework_getHandle.invoke(fw, (Object[]) null);

        if (nms_world_broadcastEntityEffect == null) {
            nms_world_broadcastEntityEffect = getMethod(nms_world.getClass(),
                    "broadcastEntityEffect");
        }

        FireworkMeta data = fw.getFireworkMeta();
        data.clearEffects();

        // Add Effects
        for (FireworkEffect effect : Effects) {
            data.addEffect(effect);
        }

        if (Power == 1234) {
            nms_firework.getClass();
        }

        // Instant
        boolean instaburst;
        if (Power == 0) {
            instaburst = true;
            /*
             * fw.remove(); InstantFirework.playFirework(location.getWorld(),
			 * location, Effects.get(0));
			 */
        } else {
            instaburst = false;
            data.setPower(Math.min(1, Power));
        }

        // Set data
        fw.setFireworkMeta(data);

        // Velocity
        if (Direction.length() > 0) {
            fw.setVelocity(Direction.normalize()
                    .multiply(DirectionPower * 0.05));
        }
        if (instaburst) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(
                    Bukkit.getPluginManager().getPlugin("MagicAssistant"),
                    new Runnable() {
                        public void run() {
                            fw.detonate();
                        }
                    }, 1L);
        }
    }

    private static Method getMethod(Class<?> cl, String method) {
        for (Method m : cl.getMethods()) {
            if (m.getName().equals(method)) {
                return m;
            }
        }
        return null;
    }
}