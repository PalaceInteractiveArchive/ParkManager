package network.palace.parkmanager.utils;

import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import org.bukkit.ChatColor;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

public class TimeUtil {
    private List<UUID> watchViewers = new ArrayList<>();

    public TimeUtil() {
        long milliseconds = System.currentTimeMillis() - ((System.currentTimeMillis() / 1000) * 1000);
        long delay = (long) Math.floor(20 - ((milliseconds * 20) / 1000));
        Core.runTaskTimer(() -> {
            String watchText = getWatchTimeText();
            watchViewers.forEach(uuid -> {
                CPlayer player = Core.getPlayerManager().getPlayer(uuid);
                if (player == null) return;
                player.getActionBar().show(watchText);
            });
        }, delay + 100, 20L);
    }

    public void selectWatch(CPlayer player) {
        if (watchViewers.add(player.getUniqueId())) player.getActionBar().show(getWatchTimeText());
    }

    public void unselectWatch(CPlayer player) {
        if (watchViewers.remove(player.getUniqueId())) player.getActionBar().show("");
    }

    public static ZonedDateTime getCurrentTime() {
        return LocalDateTime.now().atZone(TimeZone.getTimeZone("America/New_York").toZoneId());
    }

    public static String getWatchTimeText() {
        ZonedDateTime current = TimeUtil.getCurrentTime();

        int hour = current.getHour();
        int minute = current.getMinute();
        int second = current.getSecond();

        return ChatColor.YELLOW + "" + ChatColor.BOLD + "Current time in EST: " + ChatColor.GREEN +
                ((hour > 12 ? hour - 12 : (hour < 1 ? 12 : hour)) + ":" + (minute < 10 ? "0" + minute : minute) + ":" + (second < 10 ? "0" + second : second) + " " + (hour >= 12 ? "PM" : "AM"));
    }
}
