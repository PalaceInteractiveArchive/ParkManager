package us.mcmagic.magicassistant.show.handlers.schedule;

/**
 * Created by Marc on 10/29/15
 */
public enum ShowTime {
    ELEVEN, FOUR, NINE;

    public static ShowTime fromString(String s) {
        switch (s) {
            case "11":
                return ELEVEN;
            case "4":
                return FOUR;
            case "9":
                return NINE;
        }
        return null;
    }
}