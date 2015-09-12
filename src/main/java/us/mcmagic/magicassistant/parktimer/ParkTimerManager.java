package us.mcmagic.magicassistant.parktimer;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import us.mcmagic.magicassistant.MagicAssistant;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ParkTimerManager {

    private List<ParkTimer> timers;

    public ParkTimerManager() {
        MagicAssistant.getInstance().getLogger().info("(ParkTimerManager) Instantiated.");
    }

    private void run() {
        Bukkit.getScheduler().runTaskTimer(MagicAssistant.getInstance(), new Runnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    for (ParkTimer timer : timers) {
                        timer.play(player);
                    }
                }
            }
        }, 0L, 20L);
    }

    public void initialize() {
        timers = new CopyOnWriteArrayList<>();
        ConfigurationSection rootSection = MagicAssistant.config.getConfigurationSection("parktimers");
        Iterator i = rootSection.getKeys(false).iterator();
        while (i.hasNext()) {
            String name = (String) i.next();

            ConfigurationSection nextSection = rootSection.getConfigurationSection(name);
            ConfigurationSection soundSection = nextSection.getConfigurationSection("sound");

            int length = nextSection.getInt("length");
            int distance = nextSection.getInt("distance");
            Location origin = ParkTimerManager.getLocation(nextSection.getString("location"));

            String sound = soundSection.getString("name");
            float volume = 10F;
            float pitch = 10F;

            try {
                volume = Float.parseFloat(soundSection.getString("volume"));
                pitch = Float.parseFloat(soundSection.getString("pitch"));
            } catch (NullPointerException | NumberFormatException e) {
                ;
            }

            ParkTimer timer = new ParkTimer(name, sound, volume, pitch, origin, distance, length);
            addTimer(timer);
        }
        MagicAssistant.getInstance().getLogger().info("(ParkTimerManager) Loaded " + timers.size() + " timers!");
        run();
    }

    public ParkTimer getParkTimer(String uniqueName) {
        Iterator i = timers.iterator();
        ParkTimer timer;
        do {
            if (!i.hasNext()) {
                return null;
            }
            timer = (ParkTimer) i.next();
        } while(!timer.getName().equalsIgnoreCase(uniqueName));
        return timer;
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

    public static Location getLocation(String s) {
        String[] location = s.split(",");
        return new Location(Bukkit.getWorld(location[0]), Double.parseDouble(location[1]), Double.parseDouble(location[2]), Double.parseDouble(location[3]));
    }
}
