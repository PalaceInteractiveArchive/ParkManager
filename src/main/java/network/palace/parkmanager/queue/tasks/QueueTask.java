package network.palace.parkmanager.queue.tasks;

import network.palace.parkmanager.queue.QueueRide;

/**
 * Created by Marc on 9/14/15
 */
public abstract class QueueTask {
    public QueueRide ride;
    public long time;

    public QueueTask(QueueRide ride, long time) {
        this.ride = ride;
        this.time = time;
    }

    public QueueRide getRide() {
        return ride;
    }

    public long getTime() {
        return time;
    }

    public abstract void execute();
}