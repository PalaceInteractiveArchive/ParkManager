package network.palace.parkmanager.utils;

import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

/**
 * Created by Marc on 10/25/15
 */
public class TeleportUtil {
    private HashMap<UUID, Location> locations = new HashMap<>();

    public void log(Player player, Location location) {
        log(Core.getPlayerManager().getPlayer(player), location);
    }

    public void log(CPlayer player, Location location) {
        if (Core.getPlayerManager().getPlayer(player.getUniqueId()).getRank().getRankId() < Rank.KNIGHT.getRankId()) {
            return;
        }
        if (locations.containsKey(player.getUniqueId())) {
            locations.remove(player.getUniqueId());
        }
        locations.put(player.getUniqueId(), location);
    }

    public void logout(Player player) {
        locations.remove(player.getUniqueId());
    }

    public boolean back(CPlayer player) {
        if (!locations.containsKey(player.getUniqueId())) {
            return false;
        }
        final Location loc = player.getLocation();
        player.teleport(locations.get(player.getUniqueId()));
        log(player, loc);
        return true;
    }
}