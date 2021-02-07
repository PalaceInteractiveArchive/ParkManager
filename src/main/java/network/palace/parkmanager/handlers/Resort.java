package network.palace.parkmanager.handlers;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum Resort {
    WDW(0), DLR(1), USO(2);

    @Getter private final int id;

    public static Resort fromString(String s) {
        if (s == null) return WDW;
        switch (s.toLowerCase()) {
            case "wdw":
                return WDW;
            case "dlr":
                return DLR;
            case "uso":
                return USO;
        }
        return WDW;
    }

    public static Resort fromId(int id) {
        for (Resort type : values()) {
            if (type.getId() == id) return type;
        }
        return WDW;
    }
}
