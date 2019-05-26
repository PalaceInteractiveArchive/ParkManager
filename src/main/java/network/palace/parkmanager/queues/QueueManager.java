package network.palace.parkmanager.queues;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import network.palace.core.Core;
import network.palace.core.utils.TextUtil;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.utils.FileUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class QueueManager {
    private int nextId = 0;
    private List<Queue> attractions = new ArrayList<>();

    public QueueManager() {
        initialize();
    }

    public void initialize() {
        attractions.clear();
        FileUtil.FileSubsystem subsystem = ParkManager.getFileUtil().registerSubsystem("attraction");
        try {
            JsonElement element = subsystem.getFileContents("attractions");
            if (element.isJsonArray()) {
                JsonArray array = element.getAsJsonArray();
                for (JsonElement entry : array) {
                    JsonObject object = entry.getAsJsonObject();
//                    attractions.add(new Queue(nextId++, object.get("name").getAsString(), object.get("warp").getAsString(),
//                            ItemUtil.getItemFromJson(object.get("item").getAsJsonObject().toString())));
                }
            } else {
                saveToFile();
            }
            Core.logMessage("QueueManager", "Loaded " + attractions.size() + " attraction" + TextUtil.pluralize(attractions.size()) + "!");
        } catch (IOException e) {
            Core.logMessage("QueueManager", "There was an error loading the QueueManager config!");
            e.printStackTrace();
        }
    }

    public List<Queue> getQueues() {
        return new ArrayList<>(attractions);
    }

    public int getNextId() {
        return nextId++;
    }

    public Queue getQueue(int id) {
//        for (Queue attraction : getQueues()) {
//            if (attraction.getId() == id) {
//                return attraction;
//            }
//        }
        return null;
    }

    public void addQueue(Queue attraction) {
        attractions.add(attraction);
        saveToFile();
    }

    public boolean removeQueue(int id) {
        Queue attraction = getQueue(id);
        if (attraction == null) return false;
        attractions.remove(attraction);
        saveToFile();
        return true;
    }

    public void saveToFile() {
        JsonArray array = new JsonArray();
        for (Queue attraction : attractions) {
            JsonObject object = new JsonObject();
            object.addProperty("name", attraction.getName());
            object.addProperty("warp", attraction.getWarp());
//            object.add("item", ItemUtil.getJsonFromItem(attraction.getItem()));
            array.add(object);
        }
        try {
            ParkManager.getFileUtil().getSubsystem("attraction").writeFileContents("attractions", array);
        } catch (IOException e) {
            Core.logMessage("QueueManager", "There was an error writing to the QueueManager config!");
            e.printStackTrace();
        }
    }
}
