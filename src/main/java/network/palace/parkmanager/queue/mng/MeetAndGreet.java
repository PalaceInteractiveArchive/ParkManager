package network.palace.parkmanager.queue.mng;

import network.palace.parkmanager.handlers.RideCategory;
import network.palace.parkmanager.queue.handlers.QueueRide;
import org.bukkit.Location;

/**
 * Created by Marc on 3/26/16
 */
public class MeetAndGreet extends QueueRide {

    public MeetAndGreet(String name, Location station, Location spawner, int delay, int amountOfRiders, String warp,
                        RideCategory category, String shortName) {
        super(name, station, spawner, delay, amountOfRiders, warp, category, shortName);
        frozen = true;
    }
}