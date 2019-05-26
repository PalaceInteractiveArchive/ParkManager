package network.palace.parkmanager.food;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import network.palace.core.Core;
import network.palace.core.utils.ItemUtil;
import network.palace.core.utils.TextUtil;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.utils.FileUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FoodManager {
    private int nextId = 0;
    private List<FoodLocation> foodLocations = new ArrayList<>();

    public FoodManager() {
        initialize();
    }

    public void initialize() {
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
                    foodLocations.add(new FoodLocation(nextId++, object.get("name").getAsString(), object.get("warp").getAsString(),
                            ItemUtil.getItemFromJson(object.get("item").getAsJsonObject().toString())));
                }
            } else {
                saveToFile();
            }
            Core.logMessage("FoodManager", "Loaded " + foodLocations.size() + " food location" + TextUtil.pluralize(foodLocations.size()) + "!");
        } catch (IOException e) {
            Core.logMessage("FoodManager", "There was an error loading the FoodManager config!");
            e.printStackTrace();
        }
    }

    public List<FoodLocation> getFoodLocations() {
        return new ArrayList<>(foodLocations);
    }

    public int getNextId() {
        return nextId++;
    }

    public FoodLocation getFoodLocation(int id) {
        for (FoodLocation food : getFoodLocations()) {
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

    public boolean removeFoodLocation(int id) {
        FoodLocation food = getFoodLocation(id);
        if (food == null) return false;
        foodLocations.remove(food);
        saveToFile();
        return true;
    }

    public void saveToFile() {
        JsonArray array = new JsonArray();
        for (FoodLocation food : foodLocations) {
            JsonObject object = new JsonObject();
            object.addProperty("name", food.getName());
            object.addProperty("warp", food.getWarp());
            object.add("item", ItemUtil.getJsonFromItem(food.getItem()));
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
