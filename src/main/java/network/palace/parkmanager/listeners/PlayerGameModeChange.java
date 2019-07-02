package network.palace.parkmanager.listeners;

import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.parkmanager.ParkManager;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerGameModeChangeEvent;

public class PlayerGameModeChange implements Listener {

    @EventHandler
    public void onGameModeChange(PlayerGameModeChangeEvent event) {
        CPlayer player = Core.getPlayerManager().getPlayer(event.getPlayer());
        if (player == null) return;
        GameMode gameMode = event.getNewGameMode();
        if (ParkManager.getBuildUtil().isInBuildMode(player)) {
            if (!gameMode.equals(GameMode.CREATIVE) && !gameMode.equals(GameMode.SPECTATOR)) {
                event.setCancelled(true);
            }
        } else {
            if (player.getRank().getRankId() >= Rank.TRAINEEBUILD.getRankId()) {
                if (!gameMode.equals(GameMode.SURVIVAL)) {
                    event.setCancelled(true);
                }
            } else if (!gameMode.equals(GameMode.ADVENTURE)) {
                event.setCancelled(true);
            }
        }
    }
}
