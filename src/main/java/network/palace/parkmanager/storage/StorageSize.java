package network.palace.parkmanager.storage;

/**
 * Created by Marc on 10/10/15
 */
public enum StorageSize {
    SMALL(3, 0), LARGE(6, 1);

    private int rows;
    private int size;

    StorageSize(int rows, int size) {
        this.rows = rows;
        this.size = size;
    }

    public int getRows() {
        return rows;
    }

    public int getSize() {
        return size;
    }

    public static StorageSize fromInt(int i) {
        switch (i) {
            case 0:
                return SMALL;
            case 1:
                return LARGE;
        }
        return null;
    }
}