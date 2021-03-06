package network.palace.parkmanager.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import network.palace.core.Core;
import network.palace.core.utils.TextUtil;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.handlers.Park;
import network.palace.parkmanager.handlers.ParkType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ParkUtil {
    private HashMap<ParkType, Park> parks = new HashMap<>();

    public ParkUtil() {
        initialize();
    }

    public void initialize() {
        FileUtil.FileSubsystem subsystem = ParkManager.getFileUtil().getRootSubsystem();
        try {
            JsonElement element = subsystem.getFileContents("parks");
            if (element.isJsonArray()) {
                JsonArray array = element.getAsJsonArray();
                for (JsonElement entry : array) {
                    JsonObject object = entry.getAsJsonObject();

                    String id = object.get("id").getAsString();
                    World world = Bukkit.getWorld(object.get("world").getAsString());

                    ProtectedRegion region;
                    try {
                        region = WorldGuardPlugin.inst()
                                .getRegionManager(world)
                                .getRegion(object.get("region").getAsString());
                        if (region == null) {
                            throw new NullPointerException();
                        }
                    } catch (Exception e) {
                        Core.logMessage("ParkUtil", "There was an error loading the '" + id + "' park: Invalid region " +
                                object.get("region").getAsString() + " in world " + object.get("world").getAsString());
                        continue;
                    }

                    ParkType type = ParkType.fromString(id.toUpperCase());
                    parks.put(type, new Park(type, world, region));
                }
            }
            saveToFile();
            Core.logMessage("ParkUtil", "Loaded " + parks.size() + " park" + TextUtil.pluralize(parks.size()) + "!");
        } catch (IOException e) {
            Core.logMessage("ParkUtil", "There was an error loading the ParkUtil config!");
            e.printStackTrace();
        }
    }

    public List<Park> getParks() {
        return new ArrayList<>(parks.values());
    }

    public Park getPark(ParkType id) {
        for (Park park : getParks()) {
            if (park.getId().equals(id)) {
                return park;
            }
        }
        return null;
    }

    public Park getPark(Location loc) {
        for (Park park : getParks()) {
            if (!park.getWorld().getUID().equals(loc.getWorld().getUID())) continue;
            ProtectedRegion region = park.getRegion();
            if (region.getId().equals("__global__")) return park;
            if (region.contains(loc.getBlockX(), region.getMinimumPoint().getBlockY(), loc.getBlockZ())) return park;
        }
        return null;
    }

    public void addPark(Park park) {
        parks.put(park.getId(), park);
        saveToFile();
    }

    public boolean removePark(ParkType id) {
        parks.remove(id);
        saveToFile();
        return true;
    }

    public void saveToFile() {
        JsonArray array = new JsonArray();
        for (Park park : parks.values()) {
            JsonObject object = new JsonObject();
            object.addProperty("id", park.getId().name());
            object.addProperty("world", park.getWorld().getName());
            object.addProperty("region", park.getRegion().getId());
            array.add(object);
        }
        try {
            ParkManager.getFileUtil().getRootSubsystem().writeFileContents("parks", array);
        } catch (IOException e) {
            Core.logMessage("ParkUtil", "There was an error writing to parks.json!");
            e.printStackTrace();
        }
        try {
            ParkManager.getAttractionManager().initialize();
            ParkManager.getFoodManager().initialize();
            ParkManager.getQueueManager().initialize();
            ParkManager.getShopManager().initialize();
        } catch (NullPointerException ignored) {
        }
    }
}
