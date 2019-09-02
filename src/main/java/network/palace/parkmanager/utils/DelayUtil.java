package network.palace.parkmanager.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import network.palace.core.Core;
import network.palace.parkmanager.ParkManager;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class DelayUtil {
    private List<DelayEntry> entries = new ArrayList<>();

    public DelayUtil() {
        Core.runTaskTimer(ParkManager.getInstance(), () -> {
            List<DelayEntry> list = new ArrayList<>(entries);
            list.forEach(entry -> {
                if (entry.tick()) entries.remove(entry);
            });
        }, 0L, 1L);
    }

    /**
     * Log a delay entry
     * The block at 'loc' will be changed to 'type' after 'delay' ticks
     * After 20 ticks (or 1 second), the block will be set to air
     *
     * @param loc   the location of the delay entry
     * @param delay delay in ticks
     * @param type  the type of block to set to
     */
    public void logDelay(Location loc, long delay, Material type) {
        entries.add(new DelayEntry(loc, delay, type, true));
    }

    @Getter
    @AllArgsConstructor
    private class DelayEntry {
        private Location loc;
        private long ticks;
        private Material type;
        private boolean fromCommand;

        /**
         * Tick the delay entry
         * If 'ticks' is greater than zero, decrement it by 1
         * Otherwise, add a new delay entry to set the block to its current type in 20 ticks
         * Then, set the block to its new type
         *
         * @return true if finished, false if not
         */
        public boolean tick() {
            if (ticks <= 0) {
                if (fromCommand) entries.add(new DelayEntry(loc, 20, Material.AIR, false));
                loc.getBlock().setType(type);
                return true;
            }
            ticks--;
            return false;
        }
    }
}
