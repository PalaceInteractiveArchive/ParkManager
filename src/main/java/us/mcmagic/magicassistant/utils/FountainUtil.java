package us.mcmagic.magicassistant.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.util.Vector;
import us.mcmagic.magicassistant.MagicAssistant;
import us.mcmagic.magicassistant.show.Fountain;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FountainUtil implements Listener {
    public World _world;
    public MagicAssistant pl;
    public static List<Fountain> fountains = new ArrayList<>();
    private List<UUID> blocks = new ArrayList<>();

    public FountainUtil(MagicAssistant instance) {
        pl = instance;
        _world = Bukkit.getWorlds().get(0);
        start();
    }

    @SuppressWarnings("deprecation")
    public void start() {
        Bukkit.getScheduler().runTaskTimer(pl, new Runnable() {
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
                    FallingBlock fb = _world.spawnFallingBlock(loc, type, data);
                    fb.setVelocity(force);
                    fb.setDropItem(false);
                    f.setDuration(duration - 0.1);
                    blocks.add(fb.getUniqueId());
                }
            }
        }, 2, 2);
    }

    @EventHandler
    public void entityToBlock(EntityChangeBlockEvent event) {
        if (blocks.contains(event.getEntity().getUniqueId())) {
            event.setCancelled(true);
            event.getEntity().remove();
        }
    }
}