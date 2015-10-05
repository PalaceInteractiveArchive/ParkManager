package us.mcmagic.magicassistant.parksounds;

import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ParkSound {
    private Map<UUID, Long> playersListeningToSong = new ConcurrentHashMap<>();
    private String name;
    private String sound;
    private float volume;
    private float pitch;
    private Location origin;
    private int distance;
    private int audioLength;

    protected ParkSound(String name, String sound, float volume, float pitch, Location origin, int distance, int audioLength) {
        this.name = name;
        this.sound = sound;
        this.volume = volume;
        this.pitch = pitch;
        this.origin = origin;
        this.distance = distance;
        this.audioLength = audioLength;
    }

    public String getName() {
        return name;
    }

    public String getSound() {
        return sound;
    }

    public Location getOrigin() {
        return origin;
    }

    public int getDistance() {
        return distance;
    }

    public int getLength() {
        return audioLength;
    }

    protected Set<UUID> getPlayers() {
        return playersListeningToSong.keySet();
    }

    public void logout(Player player) {
        playersListeningToSong.remove(player.getUniqueId());
    }

    protected void play(Player player) {
        Validate.notNull(player, "Player cannot be null!");
        if (player.getLocation().distance(origin) <= distance) {
            if (playersListeningToSong.containsKey(player.getUniqueId())) {
                int elapsed = (int) (System.currentTimeMillis() - playersListeningToSong.get(player.getUniqueId())) / 1000;
                if (elapsed >= audioLength) {
                    playersListeningToSong.remove(player.getUniqueId());
                } else {
                    return;
                }
            }
            player.playSound(origin, sound, volume, pitch);
            playersListeningToSong.put(player.getUniqueId(), System.currentTimeMillis());
        }
    }
}
