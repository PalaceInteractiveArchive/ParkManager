package network.palace.parkmanager.listeners;

import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.handlers.PlayerData;
import network.palace.parkmanager.handlers.RideCount;
import network.palace.ridemanager.events.RideEndEvent;
import network.palace.ridemanager.events.RideManagerStatusEvent;
import network.palace.ridemanager.handlers.Ride;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;
import java.util.TreeMap;
import java.util.UUID;

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

    @EventHandler
    public void onRideEnd(RideEndEvent event) {
        Ride ride = event.getRide();
        ParkManager parkManager = ParkManager.getInstance();
        Optional<network.palace.parkmanager.handlers.Ride> opt = parkManager.getRides().stream().filter(r ->
                r.getShortName().equals(ride.getName())).findFirst();
        if (!opt.isPresent()) return;
        String name = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', opt.get().getDisplayName()));
        Core.runTaskAsynchronously(() -> {
            UUID[] players = event.getPlayers();
            for (UUID uuid : players) {
                CPlayer tp = Core.getPlayerManager().getPlayer(uuid);
                PlayerData data = parkManager.getPlayerData(tp.getUniqueId());
                TreeMap<String, RideCount> rides = data.getRideCounts();
                try (Connection connection = Core.getSqlUtil().getConnection()) {
                    PreparedStatement sql = connection.prepareStatement("INSERT INTO ride_counter (uuid, name, server, time) VALUES (?,?,?,?)");
                    sql.setString(1, tp.getUniqueId().toString());
                    sql.setString(2, name);
                    sql.setString(3, Core.getInstanceName());
                    sql.setInt(4, (int) (System.currentTimeMillis() / 1000));
                    sql.execute();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                if (rides.containsKey(name)) {
                    rides.get(name).addCount(1);
                } else {
                    rides.put(name, new RideCount(name, Core.getInstanceName()));
                }
                if (rides.size() >= 30) {
                    tp.giveAchievement(15);
                } else if (rides.size() >= 20) {
                    tp.giveAchievement(14);
                } else if (rides.size() >= 10) {
                    tp.giveAchievement(13);
                } else if (rides.size() >= 1) {
                    tp.giveAchievement(12);
                }
                data.setRideCounts(rides);
                tp.sendMessage(ChatColor.GREEN + "--------------" + ChatColor.GOLD + "" + ChatColor.BOLD +
                        "Ride Counter" + ChatColor.GREEN + "-------------\n" + ChatColor.YELLOW +
                        "Ride Counter for " + ChatColor.AQUA + name + ChatColor.YELLOW +
                        " is now at " + ChatColor.AQUA + data.getRideCounts().get(name).getCount() +
                        ChatColor.GREEN + "\n----------------------------------------");
                tp.playSound(tp.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 100f, 0.75f);
            }
        });
    }
}
