package us.mcmagic.parkmanager.handlers;

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
        switch (s.toLowerCase()) {
            case "wdw":
                return WDW;
            case "dlr":
                return DLR;
            case "uso":
                return USO;
        }
        return null;
    }
}
