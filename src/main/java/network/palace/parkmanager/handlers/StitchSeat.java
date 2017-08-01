package network.palace.parkmanager.handlers;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.Location;

import java.util.UUID;

/**
 * Created by Marc on 1/10/15
 */
@RequiredArgsConstructor
public class StitchSeat {
    @Getter private final int id;
    private final Location location;
    @Getter @Setter private UUID occupant = null;

    public boolean inUse() {
        return occupant != null;
    }

    public Location getLocation() {
        return location.clone();
    }

    public void clearOccupant() {
        this.occupant = null;
    }
}
