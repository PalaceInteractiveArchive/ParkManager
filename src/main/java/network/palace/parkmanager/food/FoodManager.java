package network.palace.parkmanager.food;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import network.palace.core.Core;
import network.palace.core.utils.ItemUtil;
import network.palace.core.utils.TextUtil;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.handlers.ParkType;
import network.palace.parkmanager.utils.FileUtil;
import org.bukkit.ChatColor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class FoodManager {
    private int nextId = 0;
    private List<FoodLocation> foodLocations = new ArrayList<>();

    public FoodManager() {
        initialize();
    }

    public void initialize() {
        File file = new File("plugins/ParkManager/food/locations.json");
        if (!file.exists()) {
            File newName = new File("plugins/ParkManager/food/mk.json");
            file.renameTo(newName);
        }
        foodLocations.clear();
        nextId = 0;
        FileUtil.FileSubsystem subsystem;
        if (ParkManager.getFileUtil().isSubsystemRegistered("food")) {
            subsystem = ParkManager.getFileUtil().getSubsystem("food");
        } else {
            subsystem = ParkManager.getFileUtil().registerSubsystem("food");
        }
        try {
            JsonElement element = subsystem.getFileContents("locations");
            if (element.isJsonArray()) {
                JsonArray array = element.getAsJsonArray();
                for (JsonElement entry : array) {
                    JsonObject object = entry.getAsJsonObject();
                    ParkType park;
                    if (!object.has("park")) {
                        park = ParkType.MK;
                    } else {
                        park = ParkType.fromString(object.get("park").getAsString());
                    }
                    foodLocations.add(new FoodLocation(nextId++, park, object.get("name").getAsString(),
                            object.get("warp").getAsString(), ItemUtil.getItemFromJsonNew(object.get("item").getAsJsonObject().toString())));
                }
            }
            saveToFile();
            Core.logMessage("FoodManager", "Loaded " + foodLocations.size() + " food location" + TextUtil.pluralize(foodLocations.size()) + "!");
        } catch (IOException e) {
            Core.logMessage("FoodManager", "There was an error loading the FoodManager config!");
            e.printStackTrace();
        }
    }

    public List<FoodLocation> getFoodLocations(ParkType park) {
        return foodLocations.stream().filter(food -> food.getPark().equals(park)).collect(Collectors.toList());
    }

    public int getNextId() {
        return nextId++;
    }

    public FoodLocation getFoodLocation(int id, ParkType park) {
        for (FoodLocation food : getFoodLocations(park)) {
            if (food.getId() == id) {
                return food;
            }
        }
        return null;
    }

    public void addFoodLocation(FoodLocation food) {
        foodLocations.add(food);
        saveToFile();
    }

    public boolean removeFoodLocation(int id, ParkType park) {
        FoodLocation food = getFoodLocation(id, park);
        if (food == null) return false;
        foodLocations.remove(food);
        saveToFile();
        return true;
    }

    public void saveToFile() {
        JsonArray array = new JsonArray();
        foodLocations.sort(Comparator.comparing(o -> ChatColor.stripColor(o.getName().toLowerCase())));
        for (FoodLocation food : foodLocations) {
            JsonObject object = new JsonObject();
            object.addProperty("name", food.getName());
            object.addProperty("warp", food.getWarp());
            object.add("item", ItemUtil.getJsonFromItemNew(food.getItem()));
            object.addProperty("park", food.getPark().name());
            array.add(object);
        }
        try {
            ParkManager.getFileUtil().getSubsystem("food").writeFileContents("locations", array);
        } catch (IOException e) {
            Core.logMessage("FoodManager", "There was an error writing to the FoodManager config!");
            e.printStackTrace();
        }
    }
}
