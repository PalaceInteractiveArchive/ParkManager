package network.palace.parkmanager.listeners;

import network.palace.parkmanager.ParkManager;
import network.palace.ridemanager.events.RideManagerStatusEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * @author Marc
 * @since 9/12/17
 */
public class RideListener implements Listener {

    @EventHandler
    public void onRideManagerStatus(RideManagerStatusEvent event) {
        switch (event.getStatus()) {
            case STARTING:
                ParkManager.getInstance().setupRides();
                break;
            case STOPPING:
                ParkManager.getInstance().removeRides();
                break;
        }
    }
}
