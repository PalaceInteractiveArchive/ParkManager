package us.mcmagic.magicassistant.parksounds;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import us.mcmagic.magicassistant.MagicAssistant;
import us.mcmagic.magicassistant.utils.FileUtil;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ParkSoundManager {
    private List<ParkSound> sounds;

    public ParkSoundManager() {
        MagicAssistant.getInstance().getLogger().info("(ParkSoundManager) Instantiated.");
        Bukkit.getScheduler().runTaskTimer(MagicAssistant.getInstance(), new Runnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    for (ParkSound timer : sounds) {
                        timer.play(player);
                    }
                }
            }
        }, 0L, 20L);
    }

    public void initialize() {
        sounds = new CopyOnWriteArrayList<>();
        ConfigurationSection rootSection = FileUtil.configurationYaml().getConfigurationSection("parktimers");
        if (rootSection == null) {
            return;
        }
        for (String name : rootSection.getKeys(false)) {
            ConfigurationSection nextSection = rootSection.getConfigurationSection(name);
            ConfigurationSection soundSection = nextSection.getConfigurationSection("sound");

            int length = nextSection.getInt("length");
            int distance = nextSection.getInt("distance");
            Location origin = ParkSoundManager.getLocation(nextSection.getString("location"));

            String sound = soundSection.getString("name");
            float volume = 10F;
            float pitch = 10F;

            try {
                volume = Float.parseFloat(soundSection.getString("volume"));
                pitch = Float.parseFloat(soundSection.getString("pitch"));
            } catch (NullPointerException | NumberFormatException ignored) {
            }

            ParkSound timer = new ParkSound(name, sound, volume, pitch, origin, distance, length);
            addTimer(timer);
        }
        MagicAssistant.getInstance().getLogger().info("(ParkTimerManager) Loaded " + sounds.size() + " timers!");
    }

    public ParkSound getParkSound(String uniqueName) {
        Iterator i = sounds.iterator();
        ParkSound timer;
        do {
            if (!i.hasNext()) {
                return null;
            }
            timer = (ParkSound) i.next();
        } while (!timer.getName().equalsIgnoreCase(uniqueName));
        return timer;
    }

    public boolean addTimer(ParkSound p) {
        return p != null && sounds.add(p);
    }

    public List<ParkSound> getSounds() {
        return sounds;
    }

    public void logout(Player player) {
        for (ParkSound sound : sounds) {
            sound.logout(player);
        }
    }

    public static Location getLocation(String s) {
        String[] location = s.split(",");
        return new Location(Bukkit.getWorld(location[0]), Double.parseDouble(location[1]), Double.parseDouble(location[2]), Double.parseDouble(location[3]));
    }
}
