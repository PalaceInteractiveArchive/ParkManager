package us.mcmagic.magicassistant.parktimer;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import us.mcmagic.magicassistant.MagicAssistant;

public class ParkTimer {

    private String name;
    private Sound sound;
    private float volume;
    private float pitch;
    private Location origin;
    private int radius;
    private int taskId;

    public ParkTimer(String name, Sound sound, float volume, float pitch, Location origin, int radius) {
        this.name = name;
        this.sound = sound;
        this.volume = volume;
        this.pitch = pitch;
        this.origin = origin;
        this.radius = radius;
    }

    protected void startTimer() {
        BukkitTask task = Bukkit.getScheduler().runTaskTimer(MagicAssistant.getInstance(), new Runnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    play(player);
                }
            }
        }, 0L, 3000L);
        this.taskId = task.getTaskId();
    }

    public void stopTimer() {
        Bukkit.getScheduler().cancelTask(taskId);
    }

    public String getName() {
        return name;
    }

    public Sound getSound() {
        return sound;
    }

    public Location getOrigin() {
        return origin;
    }

    public int getRadius() {
        return radius;
    }

    public boolean play(Player player) {
        Validate.notNull(player, "Player cannot be null!");
        if (player.getLocation().distance(origin) <= radius) {
            player.playSound(origin, sound, volume, pitch);
            return true;
        }
        return false;
    }
}
