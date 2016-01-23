package us.mcmagic.parkmanager.storage;

/**
 * Created by Marc on 10/10/15
 */
public enum StorageSize {
    SMALL(3,"Small"), LARGE(6,"Large");

    private int rows;
    private String size;

    StorageSize(int rows, String size) {
        this.rows = rows;
        this.size = size;
    }

    public int getRows() {
        return rows;
    }

    public String getSize() {
        return size;
    }

    public static StorageSize fromString(String s) {
        switch (s.toLowerCase()) {
            case "small":
                return SMALL;
            case "large":
                return LARGE;
        }
        return null;
    }
}