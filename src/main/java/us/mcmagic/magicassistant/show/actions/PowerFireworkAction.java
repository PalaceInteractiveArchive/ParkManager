package us.mcmagic.magicassistant.show.actions;

import net.minecraft.server.v1_8_R2.EntityFireworks;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R2.entity.CraftFirework;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.util.Vector;
import us.mcmagic.magicassistant.show.Show;

import java.util.List;

/**
 * Created by Marc on 4/29/15
 */
public class PowerFireworkAction extends ShowAction {
    private Location loc;
    private int lifetime;
    private Vector motion;
    private List<FireworkEffect> effects;

    public PowerFireworkAction(Show show, long time, Location loc, Vector motion, List<FireworkEffect> effects) {
        super(show, time);
        this.loc = loc;
        this.motion = motion;
        this.effects = effects;
    }
    //LifeTime:0,Motion:[2.0,7.5,-0.4],FireworksItem:{id:401,Count:1,tag:{Fireworks:{Explosions:[{Type:4,Flicker:0,Trail:1,Colors:[16776960],FadeColors:[10586448]}]}}}

    @Override
    public void play() {
        Firework fw = loc.getWorld().spawn(loc, Firework.class);
        FireworkMeta meta = fw.getFireworkMeta();
        for (FireworkEffect effect : effects) {
            meta.addEffect(effect);
        }
        fw.setFireworkMeta(meta);
        EntityFireworks f = ((CraftFirework) fw).getHandle();
        f.expectedLifespan = 0;
        f.motX = motion.getX();
        f.motY = motion.getY();
        f.motZ = motion.getZ();
    }
}
