package us.mcmagic.magicassistant.queue.tasks;

import org.bukkit.Material;
import us.mcmagic.magicassistant.queue.QueueRide;

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
