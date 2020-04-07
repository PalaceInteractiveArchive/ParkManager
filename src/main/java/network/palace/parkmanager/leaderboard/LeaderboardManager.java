package network.palace.parkmanager.leaderboard;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import network.palace.core.Core;
import network.palace.core.player.Rank;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.utils.FileUtil;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LeaderboardManager {
    private List<LeaderboardSign> signs = new ArrayList<>();

    public LeaderboardManager() throws IOException {
        FileUtil.FileSubsystem subsystem = ParkManager.getFileUtil().registerSubsystem("leaderboard");
        try {
            JsonElement element = subsystem.getFileContents("leaderboards");
            if (element.isJsonArray()) {
                JsonArray array = element.getAsJsonArray();
                for (JsonElement entry : array) {
                    JsonObject leaderboardObject = (JsonObject) entry;
                    String name = leaderboardObject.get("name").getAsString();
                    double x = leaderboardObject.get("x").getAsDouble();
                    double y = leaderboardObject.get("y").getAsDouble();
                    double z = leaderboardObject.get("z").getAsDouble();
                    String worldName = leaderboardObject.get("world").getAsString();
                    World world = Bukkit.getWorld(worldName);
                    if (world == null) continue;
                    signs.add(new LeaderboardSign(name, x, y, z, world));
                }
            } else {
                saveToFile();
            }
        } catch (IOException e) {
            Core.logMessage("LeaderboardManager", "There was an error loading the LeaderboardManager config!");
            e.printStackTrace();
        }
        Core.runTaskTimer(ParkManager.getInstance(), this::update, 400L, 10 * 60 * 20L);
    }

    private List<LeaderboardSign> getSigns() {
        return new ArrayList<>(signs);
    }

    public LeaderboardSign getSign(Location loc) {
        for (LeaderboardSign sign : getSigns()) {
            if (sign.getLocation().equals(loc)) {
                return sign;
            }
        }
        return null;
    }

    /**
     * Register a leaderboard sign
     *
     * @param lines the lines from the sign
     * @param block the sign block
     * @return true if successful, false if not
     */
    public boolean registerLeaderboardSign(String[] lines, Block block) {
        Sign sign = (Sign) block.getState();
        String name = (lines[1] + " " + lines[2] + " " + lines[3]).trim();
        for (LeaderboardSign s : getSigns()) {
            if (s.getRideName().equals(name)) return false;
        }
        Location loc = sign.getLocation();
        LeaderboardSign signObject = new LeaderboardSign(name, loc.getX(), loc.getY(), loc.getZ(), loc.getWorld());
        signs.add(signObject);
        signObject.update();
        saveToFile();
        return true;
    }

    public void deleteSign(Location loc) {
        for (LeaderboardSign sign : getSigns()) {
            if (!sign.getLocation().equals(loc)) continue;
            signs.remove(sign);
        }
        saveToFile();
    }

    public static void sortLeaderboardMessages(List<String> messages) {
        messages.sort((o1, o2) -> {
            String nocolor1 = ChatColor.stripColor(o1);
            String nocolor2 = ChatColor.stripColor(o2);
            int i1 = Integer.parseInt(nocolor1.substring(0, nocolor1.indexOf(":")));
            int i2 = Integer.parseInt(nocolor2.substring(0, nocolor2.indexOf(":")));
            return i2 - i1;
        });
    }

    public static String getFormattedName(Document doc) {
        return getFormattedName(UUID.fromString(doc.getString("uuid")), doc.getInteger("total"));
    }

    public static String getFormattedName(UUID uuid, int total) {
        String name;
        if (ParkManager.getPlayerUtil().getUserCache().containsKey(uuid)) {
            name = ParkManager.getPlayerUtil().getUserCache().get(uuid);
        } else {
            name = Core.getMongoHandler().uuidToUsername(uuid);
            ParkManager.getPlayerUtil().addToUserCache(uuid, name);
        }
        Rank rank = Core.getMongoHandler().getRank(uuid);
        return total + ": " + rank.getTagColor() + name;
    }

    public void update() {
        Core.logMessage("LeaderboardManager", "Updating ride counter leaderboards...");
        getSigns().forEach(LeaderboardSign::update);
    }

    public void saveToFile() {
        JsonArray array = new JsonArray();
        for (LeaderboardSign sign : signs) {
            array.add(sign.toJsonObject());
        }
        try {
            ParkManager.getFileUtil().getSubsystem("leaderboard").writeFileContents("leaderboards", array);
        } catch (IOException e) {
            Core.logMessage("LeaderboardManager", "There was an error writing to the LeaderboardManager config!");
            e.printStackTrace();
        }
    }
}
