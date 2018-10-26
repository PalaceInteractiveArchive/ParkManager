package network.palace.parkmanager.leaderboard;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import network.palace.core.Core;
import network.palace.parkmanager.listeners.PlayerInteract;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LeaderboardManager {
    private File configFile;
    private List<LeaderboardSign> signs = new ArrayList<>();

    public LeaderboardManager() throws IOException {
        configFile = new File("plugins/ParkManager/leaderboards.json");
        if (!configFile.exists()) {
            configFile.createNewFile();
        }

        JsonArray array = new Gson().fromJson(getJsonFromFile(), JsonArray.class);

        if (array == null) return;

        for (JsonElement element : array) {
            try {
                JsonObject leaderboardObject = (JsonObject) element;
                String name = leaderboardObject.get("name").getAsString();
                double x = leaderboardObject.get("x").getAsDouble();
                double y = leaderboardObject.get("y").getAsDouble();
                double z = leaderboardObject.get("z").getAsDouble();
                String worldName = leaderboardObject.get("world").getAsString();
                World world = Bukkit.getWorld(worldName);
                if (world == null) continue;
                signs.add(new LeaderboardSign(name, x, y, z, world));
            } catch (Exception ignored) {
            }
        }
        Core.runTaskTimerAsynchronously(() -> {
            List<LeaderboardSign> signs = new ArrayList<>(this.signs);
            signs.forEach(LeaderboardSign::update);
        }, 20L, 30 * 60 * 20L);
    }

    public String[] registerLeaderboardSign(String[] lines, Block block) {
        Sign sign = (Sign) block.getState();
        String name = (lines[1] + " " + lines[2] + " " + lines[3]).trim();
        Location loc = sign.getLocation();
        LeaderboardSign signObject = new LeaderboardSign(name, loc.getX(), loc.getY(), loc.getZ(), loc.getWorld());
        signs.add(signObject);
        Core.runTaskAsynchronously(signObject::update);
        saveFile();
        return new String[]{PlayerInteract.rideLeaderboard, "", "", ""};
    }

    public String getJsonFromFile() {
        try {
            BufferedReader br = new BufferedReader(new FileReader(configFile));

            StringBuilder json = new StringBuilder();

            String line;
            while ((line = br.readLine()) != null) {
                json.append(line);
            }

            return json.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "{}";
        }
    }

    public void saveFile() {
        JsonArray array = new JsonArray();
        for (LeaderboardSign sign : signs) {
            array.add(sign.toJsonObject());
        }
        Path file = Paths.get(configFile.toURI());
        try {
            Files.write(file, Collections.singletonList(array.toString()), Charset.forName("UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
