package us.mcmagic.parkmanager.handlers;

import java.util.UUID;

/**
 * Created by Greenlock28 on 1/23/2015.
 */
public class HotelRoom {
    private String hotelName;
    private int roomNumber;
    private UUID currentOccupant;
    private String occupantName;
    private long checkoutTime;
    private Warp roomWarp;
    private int cost;
    private UUID checkoutNotificationRecipient;
    private long stayLength;
    private int x;
    private int y;
    private int z;
    private boolean suite = false;

    public HotelRoom(String hotelName, int roomNumber, UUID currentOccupant, String occupantName, long checkoutTime,
                     Warp roomWarp, int cost, UUID checkoutNotificationRecipient, long stayLength, int x, int y,
                     int z, boolean suite) {
        this.hotelName = hotelName;
        this.checkoutTime = checkoutTime;
        this.currentOccupant = currentOccupant;
        this.occupantName = occupantName;
        this.roomNumber = roomNumber;
        this.roomWarp = roomWarp;
        this.cost = cost;
        this.checkoutNotificationRecipient = checkoutNotificationRecipient;
        this.stayLength = stayLength;
        this.x = x;
        this.y = y;
        this.z = z;
        this.suite = suite;
    }

    public String getName() {
        return hotelName + " #" + Integer.toString(roomNumber);
    }

    public String getHotelName() {
        return hotelName;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public UUID getCurrentOccupant() {
        return currentOccupant;
    }

    public long getCheckoutTime() {
        return checkoutTime;
    }

    public Warp getWarp() {
        return roomWarp;
    }

    public int getCost() {
        return cost;
    }

    public UUID getCheckoutNotificationRecipient() {
        return checkoutNotificationRecipient;
    }

    public void setHotelName(String hotelName) {
        this.hotelName = hotelName;
    }

    public void setRoomNumber(int roomNumber) {
        this.roomNumber = roomNumber;
    }

    public void setCurrentOccupant(UUID currentOccupant) {
        this.currentOccupant = currentOccupant;
    }

    public void setCheckoutTime(long checkoutTime) {
        this.checkoutTime = checkoutTime;
    }

    public void setWarp(Warp roomWarp) {
        this.roomWarp = roomWarp;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public void setCheckoutNotificationRecipient(UUID cnr) {
        this.checkoutNotificationRecipient = cnr;
    }

    public boolean isOccupied() {
        return currentOccupant != null;
    }

    public void decrementOccupationCooldown() {
        this.checkoutTime--;
    }

    public long getStayLength() {
        return stayLength;
    }

    public String getOccupantName() {
        return occupantName;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public void setOccupantName(String occupantName) {
        this.occupantName = occupantName;
    }

    public boolean isSuite() {
        return suite;
    }
}