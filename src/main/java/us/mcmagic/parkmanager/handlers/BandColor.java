package us.mcmagic.parkmanager.handlers;

/**
 * Created by Marc on 4/21/15
 */
public enum BandColor {
    RED("red"), ORANGE("orange"), YELLOW("yellow"), GREEN("green"), BLUE("blue"), PURPLE("purple"), PINK("pink"),
    SPECIAL1("s1"), SPECIAL2("s2"), SPECIAL3("s3"), SPECIAL4("s4"), SPECIAL5("s5");
    String name;

    BandColor(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public BandColor fromString(String s) {
        switch (s) {
            case "red":
                return RED;
            case "orange":
                return ORANGE;
            case "yellow":
                return YELLOW;
            case "green":
                return GREEN;
            case "blue":
                return BLUE;
            case "purple":
                return PURPLE;
            case "pink":
                return PINK;
            case "s1":
                return SPECIAL1;
            case "s2":
                return SPECIAL2;
            case "s3":
                return SPECIAL3;
            case "s4":
                return SPECIAL4;
            case "s5":
                return SPECIAL5;
            default:
                return BLUE;
        }
    }
}
