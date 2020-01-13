package network.palace.parkmanager.listeners;

import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import network.palace.parkmanager.ParkManager;
import network.palace.ridemanager.events.RideEndEvent;
import network.palace.ridemanager.handlers.ride.Ride;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.UUID;

public class RideListener implements Listener {

    @EventHandler
    public void onRideEnd(RideEndEvent event) {
        Ride ride = event.getRide();
        ParkManager parkManager = ParkManager.getInstance();
        String finalRideName = ChatColor.stripColor(ride.getName());
        Core.runTaskAsynchronously(() -> {
            UUID[] players = event.getPlayers();
            for (UUID uuid : players) {
                CPlayer tp = Core.getPlayerManager().getPlayer(uuid);
                if (tp == null) continue;
                ParkManager.getRideCounterUtil().logNewRide(tp, finalRideName, null);
            }
        });
    }

}
