package network.palace.parkmanager.outline;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
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
        File file = new File("plugins/ParkManager/outline.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection sec = config.getConfigurationSection("points");
        if (sec == null) {
            sec = config.createSection("points");
        }
        for (String s : sec.getKeys(false)) {
            points.add(new Point(s, sec.getInt(s + ".x"), sec.getInt(s + ".z")));
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
        File file = new File("plugins/ParkManager/outline.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection sec = config.getConfigurationSection("points");
        if (sec == null) {
            sec = config.createSection("points");
        }
        for (Point p : getPoints()) {
            sec.set(p.getName() + ".x", p.getX());
            sec.set(p.getName() + ".z", p.getZ());
        }
        try {
            config.save(file);
        } catch (IOException e) {
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
