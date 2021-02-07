package network.palace.parkmanager.handlers.storage;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StorageSize {
    SMALL(3, 0, "Small"), LARGE(6, 1, "Large");

    private final int rows;
    private final int size;
    private final String name;

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
        return SMALL;
    }
}