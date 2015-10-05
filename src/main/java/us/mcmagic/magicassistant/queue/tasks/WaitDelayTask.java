package us.mcmagic.magicassistant.queue.tasks;

import org.bukkit.entity.Player;
import us.mcmagic.magicassistant.queue.QueueRide;

/**
 * Created by Marc on 9/14/15
 */
public class WaitDelayTask extends QueueTask {
    private Player player;

    public WaitDelayTask(QueueRide ride, long time, Player player) {
        super(ride, time);
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public void execute() {
        if (!ride.getQueue().isEmpty() || !ride.getFPQueue().isEmpty()) {
            ride.moveToStation();
            ride.spawn();
        }
    }
}