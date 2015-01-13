package us.mcmagic.magicassistant.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;
import us.mcmagic.magicassistant.MagicAssistant;
import us.mcmagic.magicassistant.show.Fountain;

import java.util.ArrayList;
import java.util.List;

public class FountainUtil implements Listener {
    public World _world;
    public MagicAssistant pl;
    public static List<Fountain> fountains = new ArrayList<>();

    public FountainUtil(MagicAssistant instance) {
        pl = instance;
        _world = Bukkit.getWorlds().get(0);
        start();
    }

    @SuppressWarnings("deprecation")
    public void start() {
        Bukkit.getScheduler().runTaskTimer(pl, new Runnable() {
            public void run() {
                for (int i = 0; i < fountains.size(); i++) {
                    Fountain f = fountains.get(i);
                    double duration = f.getDuration();
                    if (duration < 0) {
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
                    fb.setMetadata("fountain", new FixedMetadataValue(pl, true));
                    f.setDuration(duration - 0.1);
                }
            }
        }, 1, 1);
    }

    @EventHandler
    public void entityToBlock(EntityChangeBlockEvent event) {
        if (event.getEntity().hasMetadata("fountain")) {
            if (event.getEntity().getMetadata("fountain").get(0).asBoolean()) {
                event.setCancelled(true);
                event.getEntity().remove();
            }
        }
    }
}