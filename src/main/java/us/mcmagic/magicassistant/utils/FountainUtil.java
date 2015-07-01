package us.mcmagic.magicassistant.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.util.Vector;
import us.mcmagic.magicassistant.MagicAssistant;
import us.mcmagic.magicassistant.handlers.PlayerData;
import us.mcmagic.magicassistant.show.EntityHider;
import us.mcmagic.magicassistant.show.Fountain;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FountainUtil implements Listener {
    public static List<Fountain> fountains = new ArrayList<>();
    private List<UUID> blocks = new ArrayList<>();
    private EntityHider hider;

    public FountainUtil() {
        hider = new EntityHider(MagicAssistant.getInstance(), EntityHider.Policy.BLACKLIST);
        start();
    }

    @SuppressWarnings("deprecation")
    public void start() {
        Bukkit.getScheduler().runTaskTimer(MagicAssistant.getInstance(), new Runnable() {
            public void run() {
                for (Fountain f : new ArrayList<>(fountains)) {
                    double duration = f.getDuration();
                    if (duration <= 0) {
                        fountains.remove(f);
                        continue;
                    }
                    Location loc = f.getLocation();
                    int type = f.getType();
                    byte data = f.getData();
                    Vector force = f.getForce();
                    FallingBlock fb = loc.getWorld().spawnFallingBlock(loc, type, data);
                    for (Player tp : Bukkit.getOnlinePlayers()) {
                        PlayerData pd = MagicAssistant.getPlayerData(tp.getUniqueId());
                        if (!pd.getFountain()) {
                            hider.hideEntity(tp, fb);
                        }
                    }
                    fb.setVelocity(force);
                    fb.setDropItem(false);
                    f.setDuration(duration - 0.1);
                    blocks.add(fb.getUniqueId());
                }
            }
        }, 0L, 2L);
    }

    @EventHandler
    public void entityToBlock(EntityChangeBlockEvent event) {
        if (blocks.contains(event.getEntity().getUniqueId())) {
            event.setCancelled(true);
            event.getEntity().remove();
        }
    }
}