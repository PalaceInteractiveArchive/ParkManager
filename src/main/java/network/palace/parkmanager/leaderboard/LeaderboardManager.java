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
    private final List<LeaderboardSign> signs = new ArrayList<>();
    private final List<SignUpdate> signUpdates = new ArrayList<>();

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
                    LeaderboardSign sign;
                    try {
                        sign = new LeaderboardSign(name, x, y, z, world);
                    } catch (Exception e) {
                        continue;
                    }
                    signs.add(sign);
                }
            } else {
                saveToFile();
            }
        } catch (IOException e) {
            Core.logMessage("LeaderboardManager", "There was an error loading the LeaderboardManager config!");
            e.printStackTrace();
        }
        Core.runTaskTimer(() -> new ArrayList<>(signUpdates).forEach(update -> {
            try {
                LeaderboardSign sign = update.getSign();
                Location loc = sign.getLocation();
                Sign s = (Sign) loc.getBlock().getState();
                String[] lines = update.getLines();
                for (int i = 0; i < lines.length; i++) {
                    s.setLine(i, lines[i]);
                }
                s.update();
            } catch (Exception e) {
                deleteSign(update.getSign().getLocation());
            }
            signUpdates.remove(update);
        }), 0L, 600L);
        Core.runTaskTimerAsynchronously(ParkManager.getInstance(), this::update, 400L, 10 * 60 * 20L);
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
        LeaderboardSign signObject;
        try {
            signObject = new LeaderboardSign(name, loc.getX(), loc.getY(), loc.getZ(), loc.getWorld());
        } catch (Exception e) {
            return false;
        }
        Core.runTaskAsynchronously(ParkManager.getInstance(), () -> {
            SignUpdate update = signObject.update();
            if (update != null) signUpdates.add(update);
            signs.add(signObject);
            saveToFile();
        });
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
        if (signs.isEmpty()) return;
        Core.logMessage("LeaderboardManager", "Updating ride counter leaderboards...");
        getSigns().forEach(sign -> {
            SignUpdate update = sign.update();
            if (update != null) signUpdates.add(update);
        });
        Core.logMessage("LeaderboardManager", "Finished updating ride counter leaderboards!");
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
