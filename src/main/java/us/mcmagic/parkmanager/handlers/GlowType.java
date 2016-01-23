package us.mcmagic.parkmanager.handlers;

import org.bukkit.Color;

/**
 * Created by Marc on 3/10/15
 */
public enum GlowType {
    RED(Color.fromRGB(170, 0, 0)), ORANGE(Color.fromRGB(255, 102, 0)), YELLOW(Color.fromRGB(255, 222, 0)),
    GREEN(Color.fromRGB(0, 153, 0)), AQUA(Color.fromRGB(0, 255, 255)), BLUE(Color.fromRGB(51, 51, 255)),
    PURPLE(Color.fromRGB(39, 31, 155)), PINK(Color.fromRGB(255, 0, 255)), WHITE(Color.fromRGB(255, 255, 255)),
    BLACK(Color.fromRGB(0, 0, 0)), DONE(Color.fromRGB(0, 0, 0));

    private Color color;

    GlowType(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    public static GlowType fromString(String s) {
        switch (s.toLowerCase()) {
            case "red":
                return RED;
            case "orange":
                return ORANGE;
            case "yellow":
                return YELLOW;
            case "green":
                return GREEN;
            case "aqua":
                return AQUA;
            case "blue":
                return BLUE;
            case "purple":
                return PURPLE;
            case "pink":
                return PINK;
            case "white":
                return WHITE;
            case "black":
                return BLACK;
            case "done":
                return DONE;
            default:
                return DONE;
        }
    }
}
