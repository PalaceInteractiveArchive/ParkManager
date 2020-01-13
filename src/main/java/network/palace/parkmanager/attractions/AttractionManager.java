package network.palace.parkmanager.attractions;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import network.palace.core.Core;
import network.palace.core.utils.ItemUtil;
import network.palace.core.utils.TextUtil;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.handlers.AttractionCategory;
import network.palace.parkmanager.handlers.Park;
import network.palace.parkmanager.handlers.ParkType;
import network.palace.parkmanager.utils.FileUtil;
import org.bukkit.ChatColor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class AttractionManager {
    private List<Attraction> attractions = new ArrayList<>();

    public AttractionManager() {
        initialize();
    }

    public void initialize() {
        attractions.clear();
        FileUtil.FileSubsystem subsystem;
        if (ParkManager.getFileUtil().isSubsystemRegistered("attraction")) {
            subsystem = ParkManager.getFileUtil().getSubsystem("attraction");
        } else {
            subsystem = ParkManager.getFileUtil().registerSubsystem("attraction");
        }
        File file = new File("plugins/ParkManager/attraction/attractions.json");
        if (file.exists()) {
            File newName = new File("plugins/ParkManager/attraction/epcot.json");
            file.renameTo(newName);
        }
        for (Park park : ParkManager.getParkUtil().getParks()) {
            try {
                JsonElement element = subsystem.getFileContents(park.getId().name().toLowerCase());
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
                        String id;
                        if (object.has("id")) {
                            id = object.get("id").getAsString();
                        } else {
                            id = object.get("warp").getAsString().toLowerCase();
                        }
                        attractions.add(new Attraction(id, park.getId(), object.get("name").getAsString(), object.get("warp").getAsString(),
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
    }

    public List<Attraction> getAttractions(ParkType park) {
        return attractions.stream().filter(attr -> attr.getPark().equals(park)).collect(Collectors.toList());
    }

    public Attraction getAttraction(String id, ParkType park) {
        for (Attraction attraction : getAttractions(park)) {
            if (attraction.getId().equals(id)) {
                return attraction;
            }
        }
        return null;
    }

    public void addAttraction(Attraction attraction) {
        attractions.add(attraction);
        saveToFile();
    }

    public boolean removeAttraction(String id, ParkType park) {
        Attraction attraction = getAttraction(id, park);
        if (attraction == null) return false;
        attractions.remove(attraction);
        saveToFile();
        return true;
    }

    public void saveToFile() {
        attractions.sort(Comparator.comparing(o -> ChatColor.stripColor(o.getName().toLowerCase())));
        for (Park park : ParkManager.getParkUtil().getParks()) {
            JsonArray array = new JsonArray();
            attractions.stream().filter(attr -> attr.getPark().equals(park.getId())).forEach(attraction -> {
                JsonObject object = new JsonObject();
                object.addProperty("id", attraction.getId());
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
            });
            try {
                ParkManager.getFileUtil().getSubsystem("attraction").writeFileContents(park.getId().name().toLowerCase(), array);
            } catch (IOException e) {
                Core.logMessage("AttractionManager", "There was an error writing to the AttractionManager config: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
