package us.mcmagic.magicassistant.storage;

/**
 * Created by Marc on 10/10/15
 */
public enum StorageSize {
    SMALL(3), LARGE(6);

    private int rows;

    StorageSize(int rows) {
        this.rows = rows;
    }

    public int getRows() {
        return rows;
    }

    public static StorageSize fromString(String s) {
        return null;
    }
}