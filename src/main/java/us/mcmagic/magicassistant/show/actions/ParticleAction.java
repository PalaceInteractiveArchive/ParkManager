package us.mcmagic.magicassistant.show.actions;

import org.bukkit.Location;
import us.mcmagic.magicassistant.show.Show;
import us.mcmagic.mcmagiccore.particles.ParticleEffect;
import us.mcmagic.mcmagiccore.particles.ParticleUtil;

/**
 * Created by Marc on 1/10/15
 */
public class ParticleAction extends ShowAction {
    public ParticleEffect effect;
    public Location location;
    public float offsetX;
    public float offsetY;
    public float offsetZ;
    public float speed;
    public int amount;

    public ParticleAction(Show show, long time, ParticleEffect effect, Location location, float offsetX, float offsetY, float offsetZ, float speed, int amount) {
        super(show, time);
        this.effect = effect;
        this.location = location;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetZ = offsetZ;
        this.speed = speed;
        this.amount = amount;
    }

    @Override
    public void play() {
        ParticleUtil.spawnParticle(effect, location, offsetX, offsetY, offsetZ, speed, amount);
    }
}
