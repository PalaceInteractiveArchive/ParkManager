package network.palace.parkmanager.handlers;

/**
 * Created by Marc on 4/7/17.
 */
public enum Resort {
    WDW(0), DLR(1), USO(2);

    private int id;

    Resort(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static Resort fromString(String s) {
        if (s == null) {
            return WDW;
        }
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
