package us.mcmagic.magicassistant.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleExitEvent;

/**
 * Created by Marc on 7/18/15
 */
public class VehicleExit implements Listener {

    @EventHandler
    public void onVehicleExit(VehicleExitEvent event) {
        event.setCancelled(true);
    }
}