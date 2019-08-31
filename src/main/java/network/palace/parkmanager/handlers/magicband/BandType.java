package network.palace.parkmanager.handlers.magicband;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;

@Getter
@AllArgsConstructor
public enum BandType {
    RED(true, ChatColor.RED + "Red"),
    ORANGE(true, ChatColor.GOLD + "Orange"),
    YELLOW(true, ChatColor.YELLOW + "Yellow"),
    GREEN(true, ChatColor.DARK_GREEN + "Green"),
    BLUE(true, ChatColor.BLUE + "Blue"),
    PURPLE(true, ChatColor.DARK_PURPLE + "Purple"),
    PINK(true, ChatColor.LIGHT_PURPLE + "Pink"),
    SORCERER_MICKEY(false, ChatColor.AQUA + "Sorcerer Mickey"),
    HAUNTED_MANSION(false, ChatColor.GRAY + "Haunted Mansion"),
    PRINCESSES(false, ChatColor.LIGHT_PURPLE + "Princesses"),
    BIG_HERO_SIX(false, ChatColor.RED + "Big Hero 6"),
    HOLIDAY(false, ChatColor.AQUA + "Holiday"),
    USO(false, ChatColor.BLUE + "Power Pass");

    boolean color;
    String name;

    public String getDBName() {
        return name().toLowerCase();
    }

    public static BandType fromString(String type) {
        for (BandType bandType : values()) {
            if (bandType.name().equalsIgnoreCase(type)) {
                return bandType;
            }
        }
        return RED;
    }
}
