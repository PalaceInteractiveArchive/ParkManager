package network.palace.parkmanager.listeners;

import network.palace.core.events.CorePlayerJoinedEvent;
import network.palace.core.player.CPlayer;
import network.palace.parkmanager.ParkManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlayerJoinAndLeave implements Listener {

    @EventHandler
    public void onPlayerJoin(CorePlayerJoinedEvent event) {
        CPlayer player = event.getPlayer();
        ParkManager.getStorageManager().handleJoin(player);
    }
}
