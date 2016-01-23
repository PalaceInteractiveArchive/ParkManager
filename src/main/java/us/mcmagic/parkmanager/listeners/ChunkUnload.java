package us.mcmagic.parkmanager.listeners;

import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;

/**
 * Created by Marc on 8/16/15
 */
public class ChunkUnload implements Listener {

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        for (Entity entity : event.getChunk().getEntities()) {
            if (entity.getType().name().toLowerCase().contains("minecart")) {
                entity.remove();
            }
        }
    }
}