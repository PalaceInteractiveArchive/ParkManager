package network.palace.parkmanager.utils;

import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.handlers.RideCount;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;

import java.util.TreeMap;

public class RideCounterUtil {

    public TreeMap<String, RideCount> getRideCounters(CPlayer player) {
        if (player.getRegistry().hasEntry("rideCounterCache"))
            return (TreeMap<String, RideCount>) player.getRegistry().getEntry("rideCounterCache");

        TreeMap<String, RideCount> rides = new TreeMap<>();
        for (Object o : Core.getMongoHandler().getRideCounterData(player.getUniqueId())) {
            Document doc = (Document) o;
            String name = doc.getString("name").trim();
            String server = doc.getString("server").replaceAll("[^A-Za-z ]", "");
            if (rides.containsKey(name) && rides.get(name).serverEquals(server)) {
                rides.get(name).addCount(1);
            } else {
                rides.put(name, new RideCount(name, server));
            }
        }
        player.getRegistry().addEntry("rideCounterCache", rides);

        return rides;
    }

    /**
     * Log a new ride in the database and modify the locally cached list of ride counters
     *
     * @param player   the player
     * @param rideName the name of the ride
     * @param sender   optional, this sender will be sent a message saying the ride counter was added, or nothing if null
     * @implNote This method references the database, so it is recommended to call it off of the main thread.
     */
    public void logNewRide(CPlayer player, String rideName, CommandSender sender) {
        Core.getMongoHandler().logRideCounter(player.getUniqueId(), rideName);

        TreeMap<String, RideCount> rides = ParkManager.getRideCounterUtil().getRideCounters(player);
        if (rides.containsKey(rideName)) {
            rides.get(rideName).addCount(1);
        } else {
            rides.put(rideName, new RideCount(rideName, Core.getServerType()));
        }

        if (rides.size() >= 30) {
            player.giveAchievement(15);
        } else if (rides.size() >= 20) {
            player.giveAchievement(14);
        } else if (rides.size() >= 10) {
            player.giveAchievement(13);
        } else if (rides.size() >= 1) {
            player.giveAchievement(12);
        }

        if (sender != null)
            sender.sendMessage(ChatColor.GREEN + "Added 1 to " + player.getName() + "'s counter for " + rideName);

        player.sendMessage(ChatColor.GREEN + "--------------" + ChatColor.GOLD + "" + ChatColor.BOLD +
                "Ride Counter" + ChatColor.GREEN + "-------------\n" + ChatColor.YELLOW +
                "Ride Counter for " + ChatColor.AQUA + rideName + ChatColor.YELLOW +
                " is now at " + ChatColor.AQUA + rides.get(rideName).getCount() +
                ChatColor.GREEN + "\n----------------------------------------");
        player.playSound(player.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 100f, 0.75f);
    }
}
