package network.palace.parkmanager.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;
import network.palace.core.Core;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.handlers.Resort;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import java.io.IOException;

@Getter
public class ConfigUtil {
    private Location spawn;
    private String joinMessage;
    private boolean spawnOnJoin;
    private boolean warpOnJoin;
    private Resort resort;

    public ConfigUtil() {
        FileUtil.FileSubsystem subsystem;
        if (ParkManager.getFileUtil().isSubsystemRegistered("config")) {
            subsystem = ParkManager.getFileUtil().getSubsystem("config");
        } else {
            subsystem = ParkManager.getFileUtil().registerSubsystem("config");
        }
        try {
            JsonElement element = subsystem.getFileContents("config");
            if (element.isJsonObject()) {
                JsonObject configObject = element.getAsJsonObject();
                try {
                    spawn = FileUtil.getLocation(configObject.getAsJsonObject("spawn"));
                } catch (Exception e) {
                    spawn = null;
                }
                if (configObject.has("join-message")) {
                    joinMessage = configObject.get("join-message").getAsString();
                } else {
                    joinMessage = ChatColor.GREEN + "Welcome to " + ChatColor.AQUA + "" + ChatColor.BOLD + Core.getServerType() + "!";
                }
                if (configObject.has("spawn-on-join")) {
                    spawnOnJoin = configObject.get("spawn-on-join").getAsBoolean();
                } else {
                    spawnOnJoin = false;
                }
                if (configObject.has("warp-on-join")) {
                    warpOnJoin = configObject.get("warp-on-join").getAsBoolean();
                } else {
                    warpOnJoin = true;
                }
                if (configObject.has("resort")) {
                    resort = Resort.fromString(configObject.get("resort").getAsString());
                } else {
                    resort = Resort.WDW;
                }
            }
            saveToFile();
            Core.logMessage("ConfigUtil", "Loaded config settings! This is a " + resort.name() + " server!");
        } catch (IOException e) {
            Core.logMessage("ConfigUtil", "There was an error loading the ConfigUtil config!");
            e.printStackTrace();
        }
    }

    public void setSpawn(Location loc) {
        this.spawn = loc;
        saveToFile();
    }

    public void setJoinMessage(String msg) {
        this.joinMessage = msg;
        saveToFile();
    }

    public void setSpawnOnJoin(boolean b) {
        this.spawnOnJoin = b;
        saveToFile();
    }

    public void setWarpOnJoin(boolean b) {
        this.warpOnJoin = b;
        saveToFile();
    }

    private void saveToFile() {
        JsonObject object = new JsonObject();
        object.add("spawn", FileUtil.getJson(spawn));
        object.addProperty("join-message", joinMessage);
        object.addProperty("spawn-on-join", spawnOnJoin);
        object.addProperty("warp-on-join", warpOnJoin);
        object.addProperty("resort", resort.name());
        try {
            ParkManager.getFileUtil().getSubsystem("config").writeFileContents("config", object);
        } catch (IOException e) {
            Core.logMessage("ConfigUtil", "There was an error writing to the ConfigUtil config!");
            e.printStackTrace();
        }
    }
}
