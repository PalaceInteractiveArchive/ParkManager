package network.palace.parkmanager.handlers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public enum QueueType {
    BLOCK("This type of queue spawns in a redstone block at a specified location when players are brought in"),
    CAROUSEL("A carousel with 24 horses that rotate around a central location"),
    TEACUPS("18 teacups spin on three plates around a central location"),
    AERIALCAROUSEL("Like a carousel, but you go up and down too"),
    FILE("The ride vehicle will follow a pre-determined path of actions along with added show elements and speed changes");

    @Getter @Setter String description;

    public static QueueType fromString(String s) {
        for (QueueType type : values()) {
            if (type.name().equalsIgnoreCase(s)) {
                return type;
            }
        }
        return null;
    }
}
