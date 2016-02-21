package us.mcmagic.parkmanager.pin;

import org.bukkit.ChatColor;

/**
 * Created by Marc on 2/19/16
 */
public enum PinRarity {
    COMMON(ChatColor.GREEN), RARE(ChatColor.BLUE);

    private ChatColor color;

    PinRarity(ChatColor color) {
        this.color = color;
    }

    public ChatColor getColor() {
        return color;
    }

    public String getTitle() {
        return color + capFirst(name());
    }

    private String capFirst(String name) {
        char[] array = name.toCharArray();
        if (array.length == 0) {
            return "";
        }
        array[0] = Character.toUpperCase(name.charAt(0));
        return String.valueOf(array);
    }
}