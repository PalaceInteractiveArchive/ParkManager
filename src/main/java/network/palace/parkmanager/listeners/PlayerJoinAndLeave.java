package network.palace.parkmanager.listeners;

import network.palace.core.Core;
import network.palace.core.events.CorePlayerJoinedEvent;
import network.palace.core.player.CPlayer;
import network.palace.parkmanager.ParkManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class PlayerJoinAndLeave implements Listener {

    @EventHandler
    public void onPlayerJoin(CorePlayerJoinedEvent event) {
        CPlayer player = event.getPlayer();
        ParkManager.getStorageManager().handleJoin(player);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        handleDisconnect(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerKick(PlayerKickEvent event) {
        handleDisconnect(event.getPlayer().getUniqueId());
    }

    private void handleDisconnect(UUID uuid) {
        CPlayer player = Core.getPlayerManager().getPlayer(uuid);
        if (player == null) return;

        ParkManager.getStorageManager().logout(player);
    }
}
