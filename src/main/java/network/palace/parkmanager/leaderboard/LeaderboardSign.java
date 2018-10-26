package network.palace.parkmanager.leaderboard;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import network.palace.core.Core;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.listeners.PlayerInteract;
import org.bson.Document;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Sign;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class LeaderboardSign {
    private final String rideName;
    private final double x;
    private final double y;
    private final double z;
    private final World world;
    private HashMap<UUID, Integer> cachedMap = new HashMap<>();

    public Location getLocation() {
        return new Location(world, x, y, z);
    }

    public void update() {
        List<Document> list = Core.getMongoHandler().getRideCounterLeaderboard(rideName, 10);
        cachedMap.clear();
        for (Document doc : list) {
            UUID uuid = UUID.fromString(doc.getString("uuid"));
            int amount = doc.getInteger("total");
            cachedMap.put(uuid, amount);
        }
        Sign s;
        try {
            s = ((Sign) getLocation().getBlock().getState());
        } catch (Exception e) {
            return;
        }
        s.setLine(0, PlayerInteract.rideLeaderboard);
        if (list.size() >= 1) {
            Document doc = list.get(0);
            UUID uuid = UUID.fromString(doc.getString("uuid"));
            String name;
            if (ParkManager.getInstance().getUserCache().containsKey(uuid)) {
                name = ParkManager.getInstance().getUserCache().get(uuid);
            } else {
                name = Core.getMongoHandler().uuidToUsername(uuid);
                ParkManager.getInstance().addToUserCache(uuid, name);
            }
            String line = doc.getInteger("total") + ": " + name;
            s.setLine(1, line);
        }
        if (list.size() >= 2) {
            Document doc = list.get(1);
            UUID uuid = UUID.fromString(doc.getString("uuid"));
            String name;
            if (ParkManager.getInstance().getUserCache().containsKey(uuid)) {
                name = ParkManager.getInstance().getUserCache().get(uuid);
            } else {
                name = Core.getMongoHandler().uuidToUsername(uuid);
                ParkManager.getInstance().addToUserCache(uuid, name);
            }
            String line = doc.getInteger("total") + ": " + name;
            s.setLine(2, line);
        }
        if (list.size() >= 3) {
            Document doc = list.get(2);
            UUID uuid = UUID.fromString(doc.getString("uuid"));
            String name;
            if (ParkManager.getInstance().getUserCache().containsKey(uuid)) {
                name = ParkManager.getInstance().getUserCache().get(uuid);
            } else {
                name = Core.getMongoHandler().uuidToUsername(uuid);
                ParkManager.getInstance().addToUserCache(uuid, name);
            }
            String line = doc.getInteger("total") + ": " + name;
            s.setLine(3, line);
        }
        Core.runTask(s::update);
    }

    public JsonObject toJsonObject() {
        JsonObject object = new JsonObject();
        object.addProperty("name", rideName);
        object.addProperty("x", x);
        object.addProperty("y", y);
        object.addProperty("z", z);
        object.addProperty("world", world.getName());
        return object;
    }
}
