package network.palace.parkmanager.storage;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by Marc on 10/10/15
 */
@AllArgsConstructor
public enum StorageSize {
    SMALL(3, 0, "Small"), LARGE(6, 1, "Large");

    @Getter private int rows;
    @Getter private int size;
    @Getter private String name;

    public int getSlots() {
        return rows * 9;
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