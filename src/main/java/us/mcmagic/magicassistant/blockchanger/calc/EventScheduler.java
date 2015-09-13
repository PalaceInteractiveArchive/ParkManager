package us.mcmagic.magicassistant.blockchanger.calc;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import us.mcmagic.magicassistant.blockchanger.calc.events.ChunkPostProcessingEvent;
import us.mcmagic.magicassistant.blockchanger.calc.events.ItemConvertingEvent;
import us.mcmagic.magicassistant.blockchanger.calc.lookup.SegmentLookup;


public class EventScheduler {

    private PluginManager manager;

    public EventScheduler(PluginManager manager) {
        super();
        this.manager = manager;
    }

    public void computeItemConversion(ItemStack[] stacks, Player player, boolean fromInventory) {
        ItemConvertingEvent event = new ItemConvertingEvent(stacks, player, fromInventory);
        manager.callEvent(event);
    }

    /**
     * Retrieve the lookup table given this player and the provided list of block coordinates.
     *
     * @param baseLookup - the default lookup table.
     * @param player     - the current player.
     * @param chunkX     - current chunk x position.
     * @param chunkZ     - current chunk y position.
     * @return A conversion lookup table.
     */
    public SegmentLookup getChunkConversion(SegmentLookup baseLookup, Player player, int chunkX, int chunkZ) {
        ChunkPostProcessingEvent event = new ChunkPostProcessingEvent(player, chunkX, chunkZ, baseLookup);
        manager.callEvent(event);
        return event.getLookup();
    }
}
