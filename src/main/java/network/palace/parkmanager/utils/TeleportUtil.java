package network.palace.parkmanager.utils;

import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class TeleportUtil {
    private HashMap<UUID, Location> locations = new HashMap<>();

    /**
     * Log the player's current location for /back
     *
     * @param player the player
     */
    public void log(CPlayer player) {
        log(player, player.getLocation());
    }

    /**
     * Log a location for a player for /back
     *
     * @param player   the player
     * @param location the location
     */
    public void log(CPlayer player, Location location) {
        if (player.getRank().getRankId() >= Rank.CM.getRankId()) locations.put(player.getUniqueId(), location);
    }

    /**
     * Remove stored location data
     *
     * @param player the player
     */
    public void logout(Player player) {
        locations.remove(player.getUniqueId());
    }

    /**
     * Teleport a player to their saved /back location
     *
     * @param player the player
     * @return true if successful, false if no location was found
     */
    public boolean back(CPlayer player) {
        Location back = locations.remove(player.getUniqueId());
        if (back == null) return false;
        Location current = player.getLocation();
        player.teleport(back);
        log(player, current);
        return true;
    }
}