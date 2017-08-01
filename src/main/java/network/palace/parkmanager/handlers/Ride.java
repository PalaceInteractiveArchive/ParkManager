package network.palace.parkmanager.handlers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import network.palace.parkmanager.queue.QueueRide;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Marc on 12/22/14
 */
@AllArgsConstructor
@SuppressWarnings("deprecation")
public class Ride {
    @Getter private String displayName;
    @Getter private String warp;
    @Getter private int id;
    @Getter private byte data;
    @Getter private RideCategory category;
    @Getter private QueueRide queue;
    @Getter private String shortName;
    private boolean item;

    public ItemStack getItem() {
        return new ItemStack(id, 1, data);
    }

    public boolean hasItem() {
        return item;
    }
}