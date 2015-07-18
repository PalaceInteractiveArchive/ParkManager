package us.mcmagic.magicassistant.show.actions;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import us.mcmagic.magicassistant.MagicAssistant;
import us.mcmagic.magicassistant.handlers.PlayerData;
import us.mcmagic.magicassistant.show.Show;
import us.mcmagic.mcmagiccore.particles.ParticleEffect;
import us.mcmagic.mcmagiccore.particles.ParticleUtil;

/**
 * Created by Marc on 1/10/15
 */
public class ParticleAction extends ShowAction {
    private Show show;
    public ParticleEffect effect;
    public Location loc;
    public double offsetX;
    public double offsetY;
    public double offsetZ;
    public float speed;
    public int amount;

    public ParticleAction(Show show, long time, ParticleEffect effect, Location loc, double offsetX, double offsetY,
                          double offsetZ, float speed, int amount) {
        super(show, time);
        this.show = show;
        this.effect = effect;
        this.loc = loc;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetZ = offsetZ;
        this.speed = speed;
        this.amount = amount;
    }

    @Override
    public void play() {
        for (Player tp : show.getNearPlayers()) {
            if (tp.getLocation().distance(loc) > 50) {
                continue;
            }
            PlayerData data = MagicAssistant.getPlayerData(tp.getUniqueId());
            if (!data.getFlash()) {
                continue;
            }
            ParticleUtil.spawnParticleForPlayer(effect, loc, (float) offsetX, (float) offsetY, (float) offsetZ, speed,
                    amount, tp);
        }
    }
}
