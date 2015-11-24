package us.mcmagic.magicassistant.show;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.util.Vector;
import us.mcmagic.magicassistant.MagicAssistant;
import us.mcmagic.magicassistant.show.handlers.Fountain;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FountainManager implements Listener {
    public List<Fountain> fountains = new ArrayList<>();
    private List<UUID> blocks = new ArrayList<>();

    public FountainManager() {
        start();
    }

    @SuppressWarnings("deprecation")
    private void start() {
        final ProtocolManager manager = ProtocolLibrary.getProtocolManager();
        Bukkit.getScheduler().runTaskTimer(MagicAssistant.getInstance(), new Runnable() {
            public void run() {
                for (Fountain fon : new ArrayList<>(fountains)) {
                    double duration = fon.getDuration();
                    fon.setDuration(duration - 0.2);
                    if (duration <= 0) {
                        fountains.remove(fon);
                        continue;
                    }
                    Location loc = fon.getLocation();
                    int type = fon.getType();
                    byte data = fon.getData();
                    Vector force = fon.getForce();
                    FallingBlock fb = loc.getWorld().spawnFallingBlock(loc, type, data);
                    fb.setVelocity(force);
                    blocks.add(fb.getUniqueId());
                }
            }
        }, 0L, 4L);
    }

    @EventHandler
    public void entityToBlock(EntityChangeBlockEvent event) {
        if (blocks.remove(event.getEntity().getUniqueId())) {
            event.setCancelled(true);
            event.getEntity().remove();
        }
    }

    public void addFountain(Fountain fountain) {
        fountains.add(fountain);
    }
}