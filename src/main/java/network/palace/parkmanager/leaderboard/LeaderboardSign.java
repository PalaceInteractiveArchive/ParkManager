package network.palace.parkmanager.leaderboard;

import com.google.gson.JsonObject;
import lombok.Getter;
import network.palace.core.Core;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Getter
public class LeaderboardSign {
    private final String rideName;
    private final double x;
    private final double y;
    private final double z;
    private final World world;
    private HashMap<UUID, Integer> cachedMap = new HashMap<>();

    public LeaderboardSign(String rideName, double x, double y, double z, World world) throws Exception {
        this.rideName = rideName;
        this.x = x;
        this.y = y;
        this.z = z;
        this.world = world;
        Block b = world.getBlockAt((int) x, (int) y, (int) z);
        Sign s = ((Sign) b.getState());
        if (s == null || (!b.getType().equals(Material.SIGN) &&
                !b.getType().equals(Material.SIGN_POST) &&
                !b.getType().equals(Material.WALL_SIGN))) {
            throw new Exception();
        }
    }

    public Location getLocation() {
        return new Location(world, x, y, z);
    }

    public SignUpdate update() {
        List<Document> list = Core.getMongoHandler().getRideCounterLeaderboard(rideName, 10);
        cachedMap.clear();
        for (Document doc : list) {
            UUID uuid = UUID.fromString(doc.getString("uuid"));
            int amount = doc.getInteger("total");
            cachedMap.put(uuid, amount);
        }
        String[] lines = new String[4];
        lines[0] = ChatColor.BLUE + "[Leaderboard]";
        if (list.size() >= 1) {
            lines[1] = getLine(list.get(0));
        }
        if (list.size() >= 2) {
            lines[2] = getLine(list.get(1));
        }
        if (list.size() >= 3) {
            lines[3] = getLine(list.get(2));
        }
        return new SignUpdate(this, lines);
    }

    private String getLine(Document doc) {
        String name = LeaderboardManager.getFormattedName(doc);
        return name.substring(0, Math.min(name.length(), 18));
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
