package network.palace.parkmanager.handlers.magicband;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BandType {
    RED(true), ORANGE(true), YELLOW(true), GREEN(true), BLUE(true), PURPLE(true), PINK(true);

    boolean color;

    public static BandType fromString(String type) {
        for (BandType bandType : values()) {
            if (bandType.name().equalsIgnoreCase(type)) {
                return bandType;
            }
        }
        return RED;
    }
}
