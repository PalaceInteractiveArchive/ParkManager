package us.mcmagic.parkmanager.blockchanger.calc;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import us.mcmagic.parkmanager.blockchanger.calc.events.ChunkPostProcessingEvent;
import us.mcmagic.parkmanager.blockchanger.calc.events.ItemConvertingEvent;
import us.mcmagic.parkmanager.blockchanger.calc.lookup.SegmentLookup;


public class EventScheduler {

    public void computeItemConversion(ItemStack[] stacks, Player player, boolean fromInventory) {
        ItemConvertingEvent event = new ItemConvertingEvent(stacks, player, fromInventory);
        Bukkit.getPluginManager().callEvent(event);
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
        Bukkit.getPluginManager().callEvent(event);
        return event.getLookup();
    }
}
