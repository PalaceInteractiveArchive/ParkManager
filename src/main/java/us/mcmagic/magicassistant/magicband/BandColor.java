package us.mcmagic.magicassistant.magicband;

/**
 * Created by Marc on 12/13/14
 */
public enum BandColor {
    RED("red"), ORANGE("orange"), YELLOW("yellow"), GREEN("green"), DARKGREEN("darkgreen"), BLUE("blue"), PURPLE("purple");
    String name;

    BandColor(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
