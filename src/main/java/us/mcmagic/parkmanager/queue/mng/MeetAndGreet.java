package us.mcmagic.parkmanager.queue.mng;

import org.bukkit.Location;
import us.mcmagic.parkmanager.handlers.RideCategory;
import us.mcmagic.parkmanager.queue.QueueRide;

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