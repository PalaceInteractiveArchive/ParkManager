package network.palace.parkmanager.handlers;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ParkType {
    /* Walt Disney World */
    MK("Magic Kingdom"), EPCOT("Epcot"), DHS("Disney's Hollywood Studios"), AK("Animal Kingdom"),
    TYPHOON("Typhoon Lagoon"),
    /* Universal Orlando Resort */
    USF("Universal Studios Florida"), IOA("Islands of Adventure"),
    /* Resorts */
    CONTEMPORARY("Contemporary Resort"), POLYNESIAN("Polynesian Resort"), GRANDFLORIDIAN("Grand Floridian Resort & Spa"),
    ARTOFANIMATION("Art of Animation Resort"), POPCENTURY("Pop Century Resort"),
    /* Other */
    DCL("Disney Cruise Line"), SEASONAL("Seasonal");

    String title;

    public String getId() {
        return this.equals(EPCOT) ? "Epcot" : name();
    }

    public static String listIDs() {
        StringBuilder s = new StringBuilder();
        ParkType[] values = values();
        for (int i = 0; i < values.length; i++) {
            s.append(values[i].name().toLowerCase());
            if (i < (values.length - 1)) {
                s.append(", ");
            }
        }
        return s.toString();
    }

    public static ParkType fromString(String id) {
        for (ParkType type : values()) {
            if (type.name().equalsIgnoreCase(id)) return type;
        }
        return null;
    }
}
