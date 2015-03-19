package us.mcmagic.magicassistant.handlers;

/**
 * Created by Marc on 3/10/15
 */
public enum GlowType {
    RED, ORANGE, YELLOW, GREEN, AQUA, BLUE, PURPLE, PINK, WHITE, BLACK, DONE;

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
                return null;
        }
    }
}
