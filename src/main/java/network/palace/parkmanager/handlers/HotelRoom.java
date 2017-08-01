package network.palace.parkmanager.handlers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * Created by Greenlock28 on 1/23/2015.
 */
@Getter
@AllArgsConstructor
public class HotelRoom {
    @Setter private String hotelName;
    @Setter private int roomNumber;
    @Setter private UUID currentOccupant;
    @Setter private String occupantName;
    @Setter private long checkoutTime;
    @Setter private Warp warp;
    @Setter private int cost;
    @Setter private UUID checkoutNotificationRecipient;
    private long stayLength;
    private int x;
    private int y;
    private int z;
    private boolean suite = false;

    public String getName() {
        return hotelName + " #" + Integer.toString(roomNumber);
    }

    public boolean isOccupied() {
        return currentOccupant != null;
    }

    public void decrementOccupationCooldown() {
        this.checkoutTime--;
    }
}
