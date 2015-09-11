package us.mcmagic.magicassistant.parktimer;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import us.mcmagic.magicassistant.MagicAssistant;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ParkTimerManager {

    private  List<ParkTimer> timers;

    public ParkTimerManager() {
        MagicAssistant.getInstance().getLogger().info("(ParkTimerManage) Manager instantiated. Gathering timers...");
    }

    public void initParkTimers() {
        timers = new ArrayList<>();
        ConfigurationSection rootSection = MagicAssistant.config.getConfigurationSection("parktimers");
        Iterator i = rootSection.getKeys(false).iterator();
        while (i.hasNext()) {
            String name = (String) i.next();
            ConfigurationSection soundSection = MagicAssistant.config.getConfigurationSection(name + ".sound");
            Sound sound = ConfigUtil.getSound(soundSection.getString("name"));
            Location origin = ConfigUtil.getLocation(rootSection.getString("location"));
            int audibleRadius = rootSection.getInt("audible-radius");
            float volume = 10F;
            float pitch = 10F;
            try {
                volume = Float.parseFloat(soundSection.getString("volume"));
                pitch = Float.parseFloat(soundSection.getString("pitch"));
            } catch (NullPointerException | NumberFormatException e) {
                // Ignore & fall through
            }
            ParkTimer timer = new ParkTimer(name, sound, volume, pitch, origin, audibleRadius);
            addTimer(timer);
        }
        for (ParkTimer timer : timers) {
            timer.startTimer();
        }
        MagicAssistant.getInstance().getLogger().info("(ParkTimerManager) Loaded " + timers.size() + " timers!");
    }

    public boolean addTimer(ParkTimer p) {
        if (p == null) {
            return false;
        } else {
            return timers.add(p);
        }
    }

    public List<ParkTimer> getTimers() {
        return timers;
    }

    static final class ConfigUtil {
        private static Sound getSound(String s) {
            return Sound.valueOf(s);
        }
        private static Location getLocation(String s) {
            String[] location = s.split(",");
            return new Location(Bukkit.getWorld(location[0]), Double.parseDouble(location[1]), Double.parseDouble(location[2]), Double.parseDouble(location[3]));
        }
    }
}
