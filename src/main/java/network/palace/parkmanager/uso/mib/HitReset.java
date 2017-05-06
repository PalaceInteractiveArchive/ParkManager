package network.palace.parkmanager.uso.mib;

import org.bukkit.entity.ItemFrame;

/**
 * Created by Marc on 4/19/17.
 */
public class HitReset {
    private ItemFrame frame;
    private byte data;

    public HitReset(ItemFrame frame, byte data) {
        this.frame = frame;
        this.data = data;
    }

    public ItemFrame getItemFrame() {
        return frame;
    }

    public byte getData() {
        return data;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof HitReset)) {
            return false;
        }
        HitReset hit = (HitReset) obj;
        return hit.getItemFrame().equals(frame) && hit.getData() == data;
    }
}
