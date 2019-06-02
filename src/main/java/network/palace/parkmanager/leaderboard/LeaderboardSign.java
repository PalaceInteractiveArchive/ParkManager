package network.palace.parkmanager.leaderboard;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import network.palace.core.Core;
import org.bson.Document;
import org.bukkit.ChatColor;
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
        Sign s;
        try {
            s = ((Sign) getLocation().getBlock().getState());
        } catch (Exception e) {
            return;
        }
        if (s == null) {
            return;
        }
        s.setLine(0, ChatColor.BLUE + "[Leaderboard]");
        s.setLine(1, "");
        s.setLine(2, ChatColor.AQUA + "Updating...");
        s.setLine(3, "");
        Core.runTask(s::update);
        List<Document> list = Core.getMongoHandler().getRideCounterLeaderboard(rideName, 10);
        cachedMap.clear();
        for (Document doc : list) {
            UUID uuid = UUID.fromString(doc.getString("uuid"));
            int amount = doc.getInteger("total");
            cachedMap.put(uuid, amount);
        }
        switch (list.size()) {
            case 0:
                s.setLine(1, "");
            case 1:
                s.setLine(2, "");
            case 2:
                s.setLine(3, "");
        }
        if (list.size() >= 1) {
            s.setLine(1, getLine(list.get(0)));
        }
        if (list.size() >= 2) {
            s.setLine(2, getLine(list.get(1)));
        }
        if (list.size() >= 3) {
            s.setLine(3, getLine(list.get(2)));
        }
        Core.runTask(s::update);
    }

    private String getLine(Document doc) {
        String name = LeaderboardManager.getFormattedName(doc);
        return name.substring(0, name.length() < 18 ? name.length() : 18);
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
