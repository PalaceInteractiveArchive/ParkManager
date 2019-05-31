package network.palace.parkmanager.utils;

import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import org.bukkit.ChatColor;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.*;

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

    public static long getCurrentSecondInMillis(long time) {
        return ((time / 1000) + 1) * 1000;
    }

    public static long getCurrentSecondInMillis() {
        return getCurrentSecondInMillis(System.currentTimeMillis());
    }

    public static long getTime(String s) {
        switch (s) {
            case "6AM":
                return 0;
            case "9AM":
                return 3000;
            case "12PM":
                return 6000;
            case "3PM":
                return 9000;
            case "6PM":
                return 12000;
            case "9PM":
                return 15000;
            case "12AM":
                return 18000;
            case "3AM":
                return 21000;
        }
        return -1;
    }

    /**
     * Calculate the time from #fromDate to #toDate and return a human-readable string
     *
     * @param fromDate the starting date
     * @param toDate   the ending date
     * @return A string, such as "5 minutes 42 seconds"
     */
    public static String formatDateDiff(Calendar fromDate, Calendar toDate) {
        boolean future = false;
        if (toDate.equals(fromDate)) {
            return "Now";
        }
        if (toDate.after(fromDate)) {
            future = true;
        }
        StringBuilder sb = new StringBuilder();
        int[] types = {1, 2, 5, 11, 12, 13};

        String[] names = {"Years", "Years", "Months", "Months", "Days",
                "Days", "hr", "hr", "min", "min", "s",
                "s"};

        int accuracy = 0;
        for (int i = 0; i < types.length; i++) {
            if (accuracy > 2) {
                break;
            }
            int diff = dateDiff(types[i], fromDate, toDate, future);
            if (diff > 0) {
                accuracy++;
                sb.append(" ").append(diff).append(names[(i * 2)]);
            }
        }
        if (sb.length() == 0) {
            return "Now";
        }
        return sb.toString().trim();
    }

    private static int dateDiff(int type, Calendar fromDate, Calendar toDate, boolean future) {
        int diff = 0;
        long savedDate = fromDate.getTimeInMillis();
        while ((future) && (!fromDate.after(toDate)) || (!future)
                && (!fromDate.before(toDate))) {
            savedDate = fromDate.getTimeInMillis();
            fromDate.add(type, future ? 1 : -1);
            diff++;
        }
        diff--;
        fromDate.setTimeInMillis(savedDate);
        return diff;
    }
}
