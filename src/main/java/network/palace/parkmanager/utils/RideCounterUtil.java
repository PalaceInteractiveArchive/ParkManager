package network.palace.parkmanager.utils;

import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import network.palace.parkmanager.handlers.RideCount;
import org.bson.Document;

import java.util.TreeMap;

public class RideCounterUtil {

    public TreeMap<String, RideCount> getRideCounters(CPlayer player) {
        if (player.getRegistry().hasEntry("rideCounters"))
            return (TreeMap<String, RideCount>) player.getRegistry().getEntry("rideCounters");

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
        player.getRegistry().addEntry("rideCounters", rides);

        return rides;
    }
}
