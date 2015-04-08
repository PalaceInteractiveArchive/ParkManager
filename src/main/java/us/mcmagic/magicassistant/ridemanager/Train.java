package us.mcmagic.magicassistant.ridemanager;

import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R2.entity.CraftEntity;
import us.mcmagic.mcmagiccore.particles.ParticleEffect;
import us.mcmagic.mcmagiccore.particles.ParticleUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marc on 4/1/15
 */
public class Train {
    private List<Cart> carts = new ArrayList<>();

    public Train(Cart cart) {
        carts.add(cart);
    }

    public Train(List<Cart> carts) {
        this.carts.clear();
        for (Cart cart : carts) {
            this.carts.add(cart);
        }
    }

    public List<Cart> getCarts() {
        return carts;
    }

    public void despawn() {
        for (Cart c : carts) {
            CraftEntity e = c.getBukkitEntity();
            ParticleUtil.spawnParticle(ParticleEffect.SMOKE, e.getLocation(), 0.1f, 0.1f, 0.1f, 0, 5);
            e.getWorld().playSound(e.getLocation(), Sound.FIZZ, 10, 0);
            c.die();
        }
    }

    public void addCart(Cart cart) {
        carts.add(cart);
    }

    public void removeCart(Cart cart) {
        carts.remove(cart);
    }
}
