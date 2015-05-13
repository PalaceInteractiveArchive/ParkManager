package us.mcmagic.magicassistant.show.actions;

import net.minecraft.server.v1_8_R2.EntityFireworks;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R2.entity.CraftFirework;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.util.Vector;
import us.mcmagic.magicassistant.show.Show;

/**
 * Created by Marc on 4/29/15
 */
public class CustomFireworkAction extends ShowAction {
    private Location loc;
    private int lifetime;
    private Vector motion;

    public CustomFireworkAction(Show show, long time, Location loc, Vector motion) {
        super(show, time);
        this.loc = loc;
        this.motion = motion;
    }

    @Override
    public void play() {
        Firework fw = loc.getWorld().spawn(loc, Firework.class);
        FireworkMeta meta = fw.getFireworkMeta();
        meta.addEffect(FireworkEffect.builder().build());
        fw.setFireworkMeta(meta);
        EntityFireworks f = ((CraftFirework) fw).getHandle();
        f.expectedLifespan = lifetime;
        f.motX = motion.getX();
        f.motY = motion.getY();
        f.motZ = motion.getZ();
    }
}
