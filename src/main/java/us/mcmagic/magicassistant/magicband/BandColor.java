package us.mcmagic.magicassistant.magicband;

/**
 * Created by Marc on 12/13/14
 */
public enum BandColor {
    RED("red"), ORANGE("orange"), YELLOW("yellow"), GREEN("green"), BLUE("blue"), PURPLE("purple"), PINK("pink"), SPECIAL1("s1"), SPECIAL2("s2"), SPECIAL3("s3"), SPECIAL4("s4"), SPECIAL5("s5");
    String name;

    BandColor(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
