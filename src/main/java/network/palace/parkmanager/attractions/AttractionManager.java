package network.palace.parkmanager.attractions;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import network.palace.core.Core;
import network.palace.core.utils.ItemUtil;
import network.palace.core.utils.TextUtil;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.handlers.AttractionCategory;
import network.palace.parkmanager.utils.FileUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class AttractionManager {
    private int nextId = 0;
    private List<Attraction> attractions = new ArrayList<>();

    public AttractionManager() {
        initialize();
    }

    public void initialize() {
        attractions.clear();
        nextId = 0;
        FileUtil.FileSubsystem subsystem;
        if (ParkManager.getFileUtil().isSubsystemRegistered("attraction")) {
            subsystem = ParkManager.getFileUtil().getSubsystem("attraction");
        } else {
            subsystem = ParkManager.getFileUtil().registerSubsystem("attraction");
        }
        try {
            JsonElement element = subsystem.getFileContents("attractions");
            if (element.isJsonArray()) {
                JsonArray array = element.getAsJsonArray();
                for (JsonElement entry : array) {
                    JsonObject object = entry.getAsJsonObject();

                    JsonArray categories = object.getAsJsonArray("categories");
                    List<AttractionCategory> categoryList = new ArrayList<>();
                    categories.forEach(e -> categoryList.add(AttractionCategory.fromString(e.getAsString())));
                    UUID linkedQueue;
                    if (object.has("linked-queue")) {
                        linkedQueue = UUID.fromString(object.get("linked-queue").getAsString());
                    } else {
                        linkedQueue = null;
                    }

                    attractions.add(new Attraction(nextId++, object.get("name").getAsString(), object.get("warp").getAsString(),
                            object.get("description").getAsString(), categoryList, object.get("open").getAsBoolean(),
                            ItemUtil.getItemFromJsonNew(object.get("item").getAsJsonObject().toString()), linkedQueue));
                }
            }
            saveToFile();
            Core.logMessage("AttractionManager", "Loaded " + attractions.size() + " attraction" + TextUtil.pluralize(attractions.size()) + "!");
        } catch (IOException e) {
            Core.logMessage("AttractionManager", "There was an error loading the AttractionManager config!");
            e.printStackTrace();
        }
    }

    public List<Attraction> getAttractions() {
        return new ArrayList<>(attractions);
    }

    public int getNextId() {
        return nextId++;
    }

    public Attraction getAttraction(int id) {
        for (Attraction attraction : getAttractions()) {
            if (attraction.getId() == id) {
                return attraction;
            }
        }
        return null;
    }

    public void addAttraction(Attraction attraction) {
        attractions.add(attraction);
        saveToFile();
    }

    public boolean removeAttraction(int id) {
        Attraction attraction = getAttraction(id);
        if (attraction == null) return false;
        attractions.remove(attraction);
        saveToFile();
        return true;
    }

    public void saveToFile() {
        JsonArray array = new JsonArray();
        attractions.sort(Comparator.comparing(o -> ChatColor.stripColor(o.getName().toLowerCase())));
        for (Attraction attraction : attractions) {
            JsonObject object = new JsonObject();
            object.addProperty("name", attraction.getName());
            object.addProperty("warp", attraction.getWarp());
            object.addProperty("description", attraction.getDescription());

            JsonArray categories = new JsonArray();
            attraction.getCategories().forEach(c -> categories.add(c.getShortName()));
            object.add("categories", categories);

            object.addProperty("open", attraction.isOpen());
            object.add("item", ItemUtil.getJsonFromItemNew(attraction.getItem()));

            if (attraction.getLinkedQueue() != null) {
                object.addProperty("linked-queue", attraction.getLinkedQueue().toString());
            }
            array.add(object);
        }
        try {
            ParkManager.getFileUtil().getSubsystem("attraction").writeFileContents("attractions", array);
        } catch (IOException e) {
            Core.logMessage("AttractionManager", "There was an error writing to the AttractionManager config!");
            e.printStackTrace();
        }
    }
}
