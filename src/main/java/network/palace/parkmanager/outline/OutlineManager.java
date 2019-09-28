package network.palace.parkmanager.outline;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import network.palace.core.Core;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.utils.FileUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * @author Marc
 * @since 10/20/17
 */
public class OutlineManager {
    private HashMap<UUID, OutlineSession> map = new HashMap<>();
    private List<Point> points = new ArrayList<>();

    public OutlineManager() {
        FileUtil.FileSubsystem subsystem = ParkManager.getFileUtil().registerSubsystem("outline");
        try {
            JsonElement element = subsystem.getFileContents("points");
            if (element.isJsonArray()) {
                JsonArray array = element.getAsJsonArray();
                for (JsonElement entry : array) {
                    JsonObject object = entry.getAsJsonObject();
                    points.add(new Point(object.get("name").getAsString(), object.get("x").getAsInt(), object.get("z").getAsInt()));
                }
            } else {
                saveToFile();
            }
        } catch (IOException e) {
            Core.logMessage("OutlineManager", "There was an error loading the OutlineManager config!");
            e.printStackTrace();
        }
    }

    public OutlineSession getSession(UUID uuid) {
        OutlineSession session = map.get(uuid);
        if (session == null) {
            session = new OutlineSession(uuid);
            map.put(uuid, session);
        }
        return session;
    }

    public void removeSession(UUID uuid) {
        map.remove(uuid);
    }

    public List<Point> getPoints() {
        return new ArrayList<>(points);
    }

    public void addPoint(Point point) {
        points.add(point);
        saveToFile();
    }

    public boolean removePoint(String name) {
        boolean removed = false;
        for (Point p : getPoints()) {
            if (p.getName().equalsIgnoreCase(name)) {
                removed = points.remove(p);
            }
        }
        saveToFile();
        return removed;
    }

    private void saveToFile() {
        JsonArray array = new JsonArray();
        for (Point p : getPoints()) {
            JsonObject object = new JsonObject();
            object.addProperty("name", p.getName());
            object.addProperty("x", p.getX());
            object.addProperty("z", p.getZ());
            array.add(object);
        }
        try {
            ParkManager.getFileUtil().getSubsystem("outline").writeFileContents("points", array);
        } catch (IOException e) {
            Core.logMessage("OutlineManager", "There was an error writing to the OutlineManager config!");
            e.printStackTrace();
        }
    }

    public Point getPoint(String s) {
        for (Point p : getPoints()) {
            if (p.getName().equalsIgnoreCase(s)) {
                return p;
            }
        }
        return null;
    }
}
