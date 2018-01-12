package network.palace.parkmanager.mural;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import network.palace.core.player.CPlayer;

import java.util.UUID;

@AllArgsConstructor
@RequiredArgsConstructor
public class MuralSquare {
    @Getter private UUID paintedBy = null;
    @Getter private long paintedAt = 0;
    private final int coordinate_x;
    private final int coordinate_y;

    public int getX() {
        return coordinate_x;
    }

    public int getY() {
        return coordinate_y;
    }

    public boolean isPainted() {
        return paintedBy != null;
    }

    public void paint(CPlayer player) {
        paintedAt = System.currentTimeMillis();
        paintedBy = player.getUniqueId();
    }
}
