package us.mcmagic.parkmanager.balloon;

import org.bukkit.Material;
import org.bukkit.entity.Bat;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by Marc on 6/24/16
 */
public class BalloonManager {

    @SuppressWarnings("deprecation")
    public void spawn(Player player) {
        Bat bat = player.getWorld().spawn(player.getLocation().add(0, 2, 0), Bat.class);
        bat.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 200000, 0, true, false), true);
        bat.setLeashHolder(player);
        bat.setRemoveWhenFarAway(false);
        FallingBlock b = player.getWorld().spawnFallingBlock(player.getLocation().add(0, 2, 0), Material.WOOL, (byte) 1);
        bat.setPassenger(b);
    }
}