package us.mcmagic.parkmanager.handlers;

import org.bukkit.inventory.ItemStack;
import us.mcmagic.parkmanager.queue.QueueRide;

/**
 * Created by Marc on 12/22/14
 */
@SuppressWarnings("deprecation")
public class Ride {
    private String displayName;
    private String warp;
    private int id;
    private byte data;
    private RideCategory category;
    private QueueRide queue;
    private String shortName;
    private boolean item;

    public Ride(String displayName, String warp, int id, byte data, RideCategory category, QueueRide queue,
                String shortName, boolean item) {
        this.displayName = displayName;
        this.warp = warp;
        this.id = id;
        this.data = data;
        this.category = category;
        this.queue = queue;
        this.shortName = shortName;
        this.item = item;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getWarp() {
        return warp;
    }

    public int getId() {
        return id;
    }

    public byte getData() {
        return data;
    }

    public ItemStack getItem() {
        return new ItemStack(id, 1, data);
    }

    public RideCategory getCategory() {
        return category;
    }

    public QueueRide getQueue() {
        return queue;
    }

    public String getShortName() {
        return shortName;
    }

    public boolean hasItem() {
        return item;
    }
}