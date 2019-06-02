package network.palace.parkmanager.queues;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import network.palace.core.Core;
import network.palace.core.utils.TextUtil;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.handlers.QueueType;
import network.palace.parkmanager.utils.FileUtil;
import network.palace.parkmanager.utils.TimeUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Sign;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class QueueManager {
    private int nextId = 0;
    private List<Queue> queues = new ArrayList<>();

    public QueueManager() {
        initialize();
        long time = System.currentTimeMillis();
        long milliseconds = time - TimeUtil.getCurrentSecondInMillis(time);
        long delay = (long) Math.floor(20 - ((milliseconds * 20) / 1000));
        Core.runTaskTimer(() -> {
            long currentTime = TimeUtil.getCurrentSecondInMillis();
            queues.forEach(queue -> {
                queue.tick(currentTime);
                queue.updateSigns();
            });
        }, delay, 20L);
    }

    public void initialize() {
        queues.forEach(Queue::emptyQueue);
        queues.clear();
        nextId = 0;
        FileUtil.FileSubsystem subsystem;
        if (ParkManager.getFileUtil().isSubsystemRegistered("queue")) {
            subsystem = ParkManager.getFileUtil().getSubsystem("queue");
        } else {
            subsystem = ParkManager.getFileUtil().registerSubsystem("queue");
        }
        try {
            JsonElement element = subsystem.getFileContents("queues");
            if (element.isJsonArray()) {
                JsonArray array = element.getAsJsonArray();
                for (JsonElement entry : array) {
                    JsonObject object = entry.getAsJsonObject();

                    UUID uuid = UUID.fromString(object.get("uuid").getAsString());
                    String name = ChatColor.translateAlternateColorCodes('&', object.get("name").getAsString());
                    QueueType type = QueueType.fromString(object.get("type").getAsString());
                    Location station = FileUtil.getLocation(object.getAsJsonObject("station"));
                    List<QueueSign> signs = new ArrayList<>();

                    JsonArray signArray = object.getAsJsonArray("signs");
                    for (JsonElement signElement : signArray) {
                        signs.add(new QueueSign(FileUtil.getLocation((JsonObject) signElement), name, 0));
                    }

                    if (type.equals(QueueType.BLOCK)) {
                        Location blockLocation = FileUtil.getLocation(object.getAsJsonObject("block-location"));
                        queues.add(new BlockQueue(getNextId(), uuid, name, object.get("warp").getAsString(),
                                object.get("group-size").getAsInt(), object.get("delay").getAsInt(),
                                object.get("open").getAsBoolean(), station, signs, blockLocation));
                    }
                }
            } else {
                saveToFile();
            }
            Core.logMessage("QueueManager", "Loaded " + queues.size() + " queue" + TextUtil.pluralize(queues.size()) + "!");
        } catch (Exception e) {
            Core.logMessage("QueueManager", "There was an error loading the QueueManager config!");
            e.printStackTrace();
        }
    }

    public List<Queue> getQueues() {
        return new ArrayList<>(queues);
    }

    public int getNextId() {
        return nextId++;
    }

    public Queue getQueue(int id) {
        for (Queue queue : getQueues()) {
            if (queue.getId() == id) {
                return queue;
            }
        }
        return null;
    }

    public Queue getQueue(UUID uuid) {
        for (Queue queue : getQueues()) {
            if (queue.getUuid().equals(uuid)) {
                return queue;
            }
        }
        return null;
    }

    public void addQueue(Queue queue) {
        queues.add(queue);
        saveToFile();
    }

    public boolean removeQueue(int id) {
        Queue queue = getQueue(id);
        if (queue == null) return false;
        queues.remove(queue);
        saveToFile();
        return true;
    }

    public Queue getQueue(Sign s) {
        for (Queue queue : getQueues()) {
            for (QueueSign sign : queue.getSigns()) {
                if (sign.getLocation().equals(s.getLocation())) {
                    return queue;
                }
            }
        }
        return null;
    }

    public void saveToFile() {
        JsonArray array = new JsonArray();
        for (Queue queue : queues) {
            JsonObject object = new JsonObject();
            object.addProperty("uuid", queue.getUuid().toString());
            object.addProperty("name", queue.getName().replaceAll(ChatColor.COLOR_CHAR + "", "&"));
            object.addProperty("warp", queue.getWarp());
            object.add("station", FileUtil.getJson(queue.getStation()));
            object.addProperty("group-size", queue.getGroupSize());
            object.addProperty("delay", queue.getDelay());
            object.addProperty("open", queue.isOpen());

            JsonArray signArray = new JsonArray();
            for (QueueSign sign : queue.getSigns()) {
                Location loc = sign.getLocation();
                signArray.add(FileUtil.getJson(loc));
            }
            object.add("signs", signArray);
            object.addProperty("type", queue.getQueueType().name().toLowerCase());

            if (queue.getQueueType().equals(QueueType.BLOCK)) {
                object.add("block-location", FileUtil.getJson(((BlockQueue) queue).getBlockLocation()));
            }
            array.add(object);
        }
        try {
            ParkManager.getFileUtil().getSubsystem("queue").writeFileContents("queues", array);
        } catch (IOException e) {
            Core.logMessage("QueueManager", "There was an error writing to the QueueManager config!");
            e.printStackTrace();
        }
    }
}
