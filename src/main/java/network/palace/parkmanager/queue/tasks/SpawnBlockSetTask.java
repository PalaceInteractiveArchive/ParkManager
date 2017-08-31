package network.palace.parkmanager.queue.tasks;

import network.palace.parkmanager.queue.handlers.QueueRide;
import org.bukkit.Material;

/**
 * Created by Marc on 10/4/15
 */
public class SpawnBlockSetTask extends QueueTask {
    private final Material set;

    public SpawnBlockSetTask(QueueRide ride, long time, Material set) {
        super(ride, time);
        this.set = set;
    }

    @Override
    public void execute() {
        ride.getSpawnerBlock().setType(set);
    }
}
