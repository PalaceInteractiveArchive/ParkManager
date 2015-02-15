package us.mcmagic.magicassistant.handlers;

/**
 * Created by Greenlock28 on 1/23/2015.
 */
public class HotelRoom {
    public String hotelName;
    public int roomNumber;
    public String currentOccupant;
    public int occupationCooldown;
    public Warp roomWarp;
    public int cost;
    public String checkoutNotificationRecipient = null;

    public HotelRoom(String hotelName, int roomNumber, String currentOccupant, int occupationCooldown, Warp roomWarp,
                     int cost, String checkoutNotificationRecipient) {
        this.hotelName = hotelName;
        this.occupationCooldown = occupationCooldown;
        this.currentOccupant = currentOccupant;
        this.roomNumber = roomNumber;
        this.roomWarp = roomWarp;
        this.cost = cost;
        this.checkoutNotificationRecipient = checkoutNotificationRecipient;
    }

    public String getName() { return hotelName + " #" + Integer.toString(roomNumber); }

    public String getHotelName() { return hotelName; }

    public int getRoomNumber() { return roomNumber; }

    public String getCurrentOccupant() { return currentOccupant; }

    public int getOccupationCooldown() { return occupationCooldown; }

    public Warp getWarp() { return roomWarp; }

    public int getCost() { return cost; }

    public String getCheckoutNotificationRecipient() { return checkoutNotificationRecipient; }

    public void setHotelName(String hotelName) { this.hotelName = hotelName; }

    public void setRoomNumber(int roomNumber) { this.roomNumber = roomNumber; }

    public void setCurrentOccupant(String currentOccupant) { this.currentOccupant = currentOccupant; }

    public void setOccupationCooldown(int currentOccupationCooldown) {
        this.occupationCooldown = currentOccupationCooldown;
    }

    public void setWarp(Warp roomWarp) { this.roomWarp = roomWarp; }

    public void setCost(int cost) { this.cost = cost; }

    public void setCheckoutNotificationRecipient(String cnr) { this.checkoutNotificationRecipient = cnr; }

    public boolean isOccupied() { return currentOccupant != null; }

    public void decrementOccupationCooldown() { this.occupationCooldown--; }
}
