package network.palace.parkmanager.handlers;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ParkType {
    MK("Magic Kingdom"), EPCOT("Epcot"), DHS("Disney's Hollywood Studios"), AK("Animal Kingdom"),
    USF("Universal Studios Florida"), IOA("Islands of Adventure");

    String title;

    public String getId() {
        return this.equals(EPCOT) ? "Epcot" : name();
    }

    public static String listIDs() {
        StringBuilder s = new StringBuilder();
        ParkType[] values = values();
        for (int i = 0; i < values.length; i++) {
            s.append(values[i].name().toLowerCase());
            if (i <= (values.length - 1)) {
                s.append("/");
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
