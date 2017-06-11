package network.palace.parkmanager.storage;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Marc on 10/10/15
 */
@AllArgsConstructor
public enum StorageSize {
    SMALL(3, 0), LARGE(6, 1);

    @Getter @Setter private int rows;
    @Getter @Setter private int size;

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