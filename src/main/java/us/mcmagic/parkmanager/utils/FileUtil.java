package us.mcmagic.parkmanager.utils;

import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class FileUtil {

    private FileUtil() {
    }

    private static File blockchanger = new File("plugins/ParkManager/blockchanger.yml");
    private static File configuration = new File("plugins/ParkManager/config.yml");
    private static File menus = new File("plugins/ParkManager/menus.yml");
    private static File packs = new File("plugins/ParkManager/packs.yml");
    private static File shops = new File("plugins/ParkManager/shops.yml");
    public static File SERVICE_ACCOUNT_PKCS12_FILE = new File("plugins/ParkManager/key.p12");

    public static File blockchangerFile() {
        return blockchanger;
    }

    public static File configurationFile() {
        return configuration;
    }

    public static File menuFile() {
        return menus;
    }

    public static File packsFile() {
        return packs;
    }

    public static File shopsFile() {
        return shops;
    }

    public static YamlConfiguration configurationYaml() {
        return YamlConfiguration.loadConfiguration(configurationFile());
    }

    public static YamlConfiguration menuYaml() {
        return YamlConfiguration.loadConfiguration(menuFile());
    }

    public static YamlConfiguration packsYaml() {
        return YamlConfiguration.loadConfiguration(packsFile());
    }

    public static YamlConfiguration shopsYaml() {
        return YamlConfiguration.loadConfiguration(shopsFile());
    }

    public static void setupConfig() {
        try {
            File file = FileUtil.configurationFile();
            if (file.exists()) {
                return;
            }
            if (file.createNewFile()) {
                return;
            }
            YamlConfiguration config = configurationYaml();
            config.set("server-name", "Hub");
            config.set("transfer-inventories", "false");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void setSpawn(Location loc) {
        File file = FileUtil.configurationFile();
        YamlConfiguration config = configurationYaml();
        config.set("spawn.world", loc.getWorld().getName());
        config.set("spawn.x", loc.getX());
        config.set("spawn.y", loc.getY());
        config.set("spawn.z", loc.getZ());
        config.set("spawn.yaw", loc.getYaw());
        config.set("spawn.pitch", loc.getPitch());
        saveFile(file, config);
    }

    public static void saveFile(File file, YamlConfiguration config) {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void setHub(Location loc) {
        File file = FileUtil.configurationFile();
        YamlConfiguration config = configurationYaml();
        config.set("hub.world", loc.getWorld().getName());
        config.set("hub.x", loc.getX());
        config.set("hub.y", loc.getY());
        config.set("hub.z", loc.getZ());
        config.set("hub.yaw", loc.getYaw());
        config.set("hub.pitch", loc.getPitch());
        saveFile(file, config);
    }

    public static void setFirstJoinMessages() {
        File file = FileUtil.configurationFile();
        YamlConfiguration config = configurationYaml();
        List<String> msgs = Arrays.asList(
                "&fHey everyone, it’s &d%pl% &ffirst visit to McMagic!",
                "&d%total% Guests have visited McMagic since 2012!");
        config.set("first-join-messages", msgs);
        saveFile(file, config);
    }
}