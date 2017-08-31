package network.palace.parkmanager.queue.tasks;

import network.palace.parkmanager.queue.handlers.QueueRide;

/**
 * Created by Marc on 10/6/15
 */
public class NextRidersTask extends QueueTask {

    public NextRidersTask(QueueRide ride, long time) {
        super(ride, time);
    }

    @Override
    public void execute() {
        if (!ride.getQueue().isEmpty() || !ride.getFPQueue().isEmpty()) {
            ride.moveToStation();
            ride.spawn();
        }
    }
}