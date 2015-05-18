package us.mcmagic.magicassistant.show.actions;

import org.bukkit.Bukkit;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.Firework;
import org.bukkit.event.Listener;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.util.Vector;
import us.mcmagic.magicassistant.MagicAssistant;
import us.mcmagic.magicassistant.show.Show;

import java.util.ArrayList;

public class FireworkAction extends ShowAction implements Listener {
    public Location loc;
    public ArrayList<FireworkEffect> effects;
    public int power;
    public Vector direction;
    public double dirPower;

    public FireworkAction(Show show, long time, Location loc, ArrayList<FireworkEffect> effectList, int power, Vector dir,
                          double dirPow) {
        super(show, time);
        this.loc = loc;
        effects = effectList;
        this.power = power;
        direction = dir;
        dirPower = dirPow;
    }

    @Override
    public void play() {
        try {
            playFirework();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void playFirework() throws Exception {
        final Firework fw = loc.getWorld().spawn(loc, Firework.class);
        FireworkMeta data = fw.getFireworkMeta();
        data.clearEffects();
        // Add effects
        for (FireworkEffect effect : effects) {
            data.addEffect(effect);
        }
        // Instant
        boolean instaburst;
        if (power == 0) {
            instaburst = true;
        } else {
            instaburst = false;
            data.setPower(Math.min(1, power));
        }
        // Set data
        fw.setFireworkMeta(data);
        // Velocity
        if (direction.length() > 0) {
            fw.setVelocity(direction.normalize().multiply(dirPower * 0.05));
        }
        if (instaburst) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(MagicAssistant.getInstance(),
                    new Runnable() {
                        public void run() {
                            fw.detonate();
                        }
                    }, 1L);
        }
    }
}