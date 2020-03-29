package network.palace.parkmanager.queues;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import network.palace.core.Core;
import network.palace.core.economy.currency.CurrencyType;
import network.palace.core.player.CPlayer;
import network.palace.core.utils.TextUtil;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.handlers.QueueType;
import network.palace.parkmanager.utils.FileUtil;
import network.palace.parkmanager.utils.TimeUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class QueueManager {
    private List<Queue> queues = new ArrayList<>();

    public QueueManager() {
        initialize();
        long time = System.currentTimeMillis();
        long milliseconds = time - TimeUtil.getCurrentSecondInMillis(time);
        long delay = (long) Math.floor(20 - ((milliseconds * 20) / 1000f));
        Core.runTaskTimer(ParkManager.getInstance(), () -> {
            long currentTime = TimeUtil.getCurrentSecondInMillis();
            queues.forEach(queue -> {
                try {
                    queue.tick(currentTime);
                    queue.updateSigns();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }, delay, 20L);
    }

    public void initialize() {
        queues.forEach(q -> {
            q.emptyQueue();
            if (q instanceof PluginQueue) ((PluginQueue) q).getRide().despawn();
        });
        queues.clear();
        FileUtil.FileSubsystem subsystem;
        if (ParkManager.getFileUtil().isSubsystemRegistered("queue")) {
            subsystem = ParkManager.getFileUtil().getSubsystem("queue");
        } else {
            subsystem = ParkManager.getFileUtil().registerSubsystem("queue");
        }
        try {
            JsonElement element = subsystem.getFileContents("queues");
            boolean duplicateIdCheck = false;
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
                        JsonObject signObject = (JsonObject) signElement;
                        signs.add(new QueueSign(FileUtil.getLocation(signObject), name, signObject.has("fastpass") && signObject.get("fastpass").getAsBoolean(), 0));
                    }
                    String id;
                    if (object.has("id")) {
                        id = object.get("id").getAsString();
                    } else {
                        id = object.get("warp").getAsString().toLowerCase();
                        duplicateIdCheck = true;
                    }
                    switch (type) {
                        case BLOCK:
                            Location blockLocation = FileUtil.getLocation(object.getAsJsonObject("block-location"));
                            queues.add(new BlockQueue(id, uuid, name, object.get("warp").getAsString(),
                                    object.get("group-size").getAsInt(), object.get("delay").getAsInt(),
                                    object.get("open").getAsBoolean(), station, signs, blockLocation));
                            break;
                        case CAROUSEL:
                        case TEACUPS:
                        case AERIAL_CAROUSEL:
                        case FILE:
                            queues.add(new PluginQueue(id, uuid, name, object.get("warp").getAsString(),
                                    object.get("group-size").getAsInt(), object.get("delay").getAsInt(),
                                    object.get("open").getAsBoolean(), station, signs,
                                    FileUtil.getLocation(object.getAsJsonObject("exit")), CurrencyType.BALANCE,
                                    object.get("balance").getAsInt(), object.get("honor").getAsInt(),
                                    object.get("achievement").getAsInt(), object.getAsJsonObject("rideConfig")));
                            break;
                    }
                }
            }
            if (duplicateIdCheck) {
                for (Queue queue : queues) {
                    for (Queue search : getQueues()) {
                        if (queue.getUuid().equals(search.getUuid())) continue;
                        if (queue.getId().equals(search.getId())) {
                            int id = 2;
                            while (getQueueById(search.getId() + id) != null) {
                                id++;
                            }
                            search.setId(search.getId() + id);
                            break;
                        }
                    }
                }
            }
            saveToFile();
            Core.logMessage("QueueManager", "Loaded " + queues.size() + " queue" + TextUtil.pluralize(queues.size()) + "!");
        } catch (Exception e) {
            Core.logMessage("QueueManager", "There was an error loading the QueueManager config!");
            e.printStackTrace();
        }
    }

    public List<Queue> getQueues() {
        return new ArrayList<>(queues);
    }

    public Queue getQueueById(String id) {
        for (Queue queue : getQueues()) {
            if (queue.getId().equals(id)) {
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

    public boolean removeQueue(String id) {
        Queue queue = getQueueById(id);
        if (queue == null) return false;
        if (queue instanceof PluginQueue) ((PluginQueue) queue).getRide().despawn();
        queues.remove(queue);
        saveToFile();
        return true;
    }

    public void leaveAllQueues(CPlayer player) {
        getQueues().forEach(q -> q.leaveQueue(player, true));
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

    public Queue getQueueByName(String name) {
        name = ChatColor.stripColor(name);
        for (Queue queue : getQueues()) {
            if (ChatColor.stripColor(queue.getName()).startsWith(name)) return queue;
        }
        return null;
    }

    public void saveToFile() {
        JsonArray array = new JsonArray();
        queues.sort(Comparator.comparing(o -> ChatColor.stripColor(o.getName().toLowerCase())));
        for (Queue queue : queues) {
            JsonObject object = new JsonObject();
            object.addProperty("id", queue.getId());
            object.addProperty("uuid", queue.getUuid().toString());
            object.addProperty("name", queue.getName().replaceAll(ChatColor.COLOR_CHAR + "", "&"));
            object.addProperty("warp", queue.getWarp());
            object.add("station", FileUtil.getJson(queue.getStation()));
            object.addProperty("group-size", queue.getGroupSize());
            object.addProperty("delay", queue.getDelay());
            object.addProperty("open", queue.isOpen());

            JsonArray signArray = new JsonArray();
            for (QueueSign sign : queue.getSigns()) {
                JsonObject signObject = FileUtil.getJson(sign.getLocation());
                if (sign.isFastPassSign()) signObject.addProperty("fastpass", true);
                signArray.add(signObject);
            }
            object.add("signs", signArray);
            object.addProperty("type", queue.getQueueType().name().toLowerCase());

            switch (queue.getQueueType()) {
                case BLOCK:
                    object.add("block-location", FileUtil.getJson(((BlockQueue) queue).getBlockLocation()));
                    break;
                case CAROUSEL: {
                    object.addProperty("balance", ((PluginQueue) queue).getCurrencyAmount());
                    object.addProperty("honor", ((PluginQueue) queue).getHonorAmount());
                    object.addProperty("achievement", ((PluginQueue) queue).getAchievementId());
                    object.add("exit", FileUtil.getJson(((PluginQueue) queue).getRide().getExit()));
                    JsonObject rideConfig = new JsonObject();
                    rideConfig.addProperty("rideType", "CAROUSEL");
                    rideConfig.add("center", FileUtil.getJson(((network.palace.ridemanager.handlers.ride.flat.CarouselRide) ((PluginQueue) queue).getRide()).getCenter()));
                    object.add("rideConfig", rideConfig);
                    break;
                }
                case TEACUPS: {
                    object.addProperty("balance", ((PluginQueue) queue).getCurrencyAmount());
                    object.addProperty("honor", ((PluginQueue) queue).getHonorAmount());
                    object.addProperty("achievement", ((PluginQueue) queue).getAchievementId());
                    object.add("exit", FileUtil.getJson(((PluginQueue) queue).getRide().getExit()));
                    JsonObject rideConfig = new JsonObject();
                    rideConfig.addProperty("rideType", "TEACUPS");
                    rideConfig.add("center", FileUtil.getJson(((network.palace.ridemanager.handlers.ride.flat.TeacupsRide) ((PluginQueue) queue).getRide()).getCenter()));
                    object.add("rideConfig", rideConfig);
                    break;
                }
                case AERIAL_CAROUSEL: {
                    object.addProperty("balance", ((PluginQueue) queue).getCurrencyAmount());
                    object.addProperty("honor", ((PluginQueue) queue).getHonorAmount());
                    object.addProperty("achievement", ((PluginQueue) queue).getAchievementId());
                    object.add("exit", FileUtil.getJson(((PluginQueue) queue).getRide().getExit()));

                    JsonObject rideConfig = new JsonObject();
                    rideConfig.addProperty("rideType", "AERIAL_CAROUSEL");
                    rideConfig.add("center", FileUtil.getJson(((network.palace.ridemanager.handlers.ride.flat.AerialCarouselRide) ((PluginQueue) queue).getRide()).getCenter()));
                    rideConfig.addProperty("aerialRadius", ((network.palace.ridemanager.handlers.ride.flat.AerialCarouselRide) ((PluginQueue) queue).getRide()).getAerialRadius());
                    rideConfig.addProperty("supportRadius", ((network.palace.ridemanager.handlers.ride.flat.AerialCarouselRide) ((PluginQueue) queue).getRide()).getSupportRadius());
                    rideConfig.addProperty("small", ((network.palace.ridemanager.handlers.ride.flat.AerialCarouselRide) ((PluginQueue) queue).getRide()).isSmall());
                    rideConfig.addProperty("supportAngle", ((network.palace.ridemanager.handlers.ride.flat.AerialCarouselRide) ((PluginQueue) queue).getRide()).getSupportAngle());
                    rideConfig.addProperty("height", ((network.palace.ridemanager.handlers.ride.flat.AerialCarouselRide) ((PluginQueue) queue).getRide()).getHeight());
                    rideConfig.addProperty("movein", ((network.palace.ridemanager.handlers.ride.flat.AerialCarouselRide) ((PluginQueue) queue).getRide()).getMovein());

                    object.add("rideConfig", rideConfig);
                    break;
                }
                case FILE: {
                    object.addProperty("balance", ((PluginQueue) queue).getCurrencyAmount());
                    object.addProperty("honor", ((PluginQueue) queue).getHonorAmount());
                    object.addProperty("achievement", ((PluginQueue) queue).getAchievementId());
                    object.add("exit", FileUtil.getJson(((PluginQueue) queue).getRide().getExit()));
                    JsonObject rideConfig = new JsonObject();
                    rideConfig.addProperty("rideType", "FILE");
                    String fileName = ((network.palace.ridemanager.handlers.ride.file.FileRide) ((PluginQueue) queue).getRide()).getRideFile().getName();
                    rideConfig.addProperty("file", fileName.substring(0, fileName.indexOf('.')));
                    object.add("rideConfig", rideConfig);
                    break;
                }
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

    public boolean chargeFastPass(CPlayer player) {
        int newCount = ((int) player.getRegistry().getEntry("fastPassCount")) - 1;
        if (newCount < 0) return false;
        player.getRegistry().addEntry("fastPassCount", newCount);
        Core.runTaskAsynchronously(ParkManager.getInstance(), () -> Core.getMongoHandler().chargeFastPass(player.getUniqueId(), 1));
        return true;
    }

    public void displaySignParticles(CPlayer player, Sign s) {
        org.bukkit.material.Sign sign = (org.bukkit.material.Sign) s.getData();
        BlockFace attached = sign.getAttachedFace();
        if (attached.equals(BlockFace.DOWN)) return;
        Location loc = s.getLocation(), c1, c2, c3, c4;
        switch (attached.getOppositeFace()) {
            case NORTH:
                c1 = loc.clone().add(0.95, 0.75, 0.9);
                c2 = loc.clone().add(0.05, 0.75, 0.9);
                c3 = loc.clone().add(0.95, 0.25, 0.9);
                c4 = loc.clone().add(0.05, 0.25, 0.9);
                for (int i = 0; i < 9; i++) {
                    // Top row
                    player.getParticles().send(c1.add(-0.1, 0, 0), Particle.SPELL_WITCH, 1);
                }
                for (int i = 0; i < 5; i++) {
                    // Right side
                    player.getParticles().send(c2.add(0, -0.1, 0), Particle.SPELL_WITCH, 1);
                }
                for (int i = 0; i < 5; i++) {
                    // Left side
                    player.getParticles().send(c3.add(0, 0.1, 0), Particle.SPELL_WITCH, 1);
                }
                for (int i = 0; i < 9; i++) {
                    // Bottom row
                    player.getParticles().send(c4.add(0.1, 0, 0), Particle.SPELL_WITCH, 1);
                }
                break;
            case EAST:
                c1 = loc.clone().add(0.1, 0.75, 0.95);
                c2 = loc.clone().add(0.1, 0.75, 0.05);
                c3 = loc.clone().add(0.1, 0.25, 0.95);
                c4 = loc.clone().add(0.1, 0.25, 0.05);
                for (int i = 0; i < 9; i++) {
                    // Top row
                    player.getParticles().send(c1.add(0, 0, -0.1), Particle.SPELL_WITCH, 1);
                }
                for (int i = 0; i < 5; i++) {
                    // Right side
                    player.getParticles().send(c2.add(0, -0.1, 0), Particle.SPELL_WITCH, 1);
                }
                for (int i = 0; i < 5; i++) {
                    // Left side
                    player.getParticles().send(c3.add(0, 0.1, 0), Particle.SPELL_WITCH, 1);
                }
                for (int i = 0; i < 9; i++) {
                    // Bottom row
                    player.getParticles().send(c4.add(0, 0, 0.1), Particle.SPELL_WITCH, 1);
                }
                break;
            case SOUTH:
                c1 = loc.clone().add(0.05, 0.75, 0.1);
                c2 = loc.clone().add(0.95, 0.75, 0.1);
                c3 = loc.clone().add(0.05, 0.25, 0.1);
                c4 = loc.clone().add(0.95, 0.25, 0.1);
                for (int i = 0; i < 9; i++) {
                    // Top row
                    player.getParticles().send(c1.add(0.1, 0, 0), Particle.SPELL_WITCH, 1);
                }
                for (int i = 0; i < 5; i++) {
                    // Right side
                    player.getParticles().send(c2.add(0, -0.1, 0), Particle.SPELL_WITCH, 1);
                }
                for (int i = 0; i < 5; i++) {
                    // Left side
                    player.getParticles().send(c3.add(0, 0.1, 0), Particle.SPELL_WITCH, 1);
                }
                for (int i = 0; i < 9; i++) {
                    // Bottom row
                    player.getParticles().send(c4.add(-0.1, 0, 0), Particle.SPELL_WITCH, 1);
                }
                break;
            case WEST:
                c1 = loc.clone().add(0.9, 0.75, 0.05);
                c2 = loc.clone().add(0.9, 0.75, 0.95);
                c3 = loc.clone().add(0.9, 0.25, 0.05);
                c4 = loc.clone().add(0.9, 0.25, 0.95);
                for (int i = 0; i < 9; i++) {
                    // Top row
                    player.getParticles().send(c1.add(0, 0, 0.1), Particle.SPELL_WITCH, 1);
                }
                for (int i = 0; i < 5; i++) {
                    // Right side
                    player.getParticles().send(c2.add(0, -0.1, 0), Particle.SPELL_WITCH, 1);
                }
                for (int i = 0; i < 5; i++) {
                    // Left side
                    player.getParticles().send(c3.add(0, 0.1, 0), Particle.SPELL_WITCH, 1);
                }
                for (int i = 0; i < 9; i++) {
                    // Bottom row
                    player.getParticles().send(c4.add(0, 0, -0.1), Particle.SPELL_WITCH, 1);
                }
                break;
        }
    }
}
