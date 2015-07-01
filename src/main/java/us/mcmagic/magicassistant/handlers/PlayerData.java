package us.mcmagic.magicassistant.handlers;

import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Marc on 12/13/14
 */
public class PlayerData {
    private UUID uuid;
    private boolean dvc;
    private ChatColor bandName;
    private BandColor bandColor;
    private List<UUID> friends;
    private boolean special;
    private boolean flash;
    private boolean visibility;
    private boolean fountain;
    private boolean hotel;
    private int fastpass;

    public PlayerData(UUID uuid, boolean dvc, ChatColor bandName, BandColor bandColor, List<UUID> friends,
                      boolean special, boolean flash, boolean visibility, boolean fountain, boolean hotel, int fastpass) {
        this.uuid = uuid;
        this.dvc = dvc;
        this.bandName = bandName;
        this.bandColor = bandColor;
        this.friends = friends;
        this.special = special;
        this.flash = flash;
        this.visibility = visibility;
        this.fountain = fountain;
        this.hotel = hotel;
        this.fastpass = fastpass;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public boolean isDVC() {
        return dvc;
    }

    public ChatColor getBandName() {
        return bandName;
    }

    public BandColor getBandColor() {
        return bandColor;
    }

    public List<UUID> getFriendList() {
        return new ArrayList<>(friends);
    }

    public void setBandColor(BandColor color) {
        this.bandColor = color;
    }

    public boolean getFlash() {
        return flash;
    }

    public void setFlash(boolean flash) {
        this.flash = flash;
    }

    public boolean getFountain() {
        return fountain;
    }

    public void setFountain(boolean fountain) {
        this.fountain = fountain;
    }

    public void setBandColor(Material color) {
        switch (color) {
            case PAPER:
                this.bandColor = BandColor.SPECIAL1;
            case IRON_BARDING:
                this.bandColor = BandColor.SPECIAL2;
            case GOLD_BARDING:
                this.bandColor = BandColor.SPECIAL3;
            case DIAMOND_BARDING:
                this.bandColor = BandColor.SPECIAL4;
            case GHAST_TEAR:
                this.bandColor = BandColor.SPECIAL5;
            default:
                this.bandColor = BandColor.BLUE;
        }
    }

    public void setBandName(ChatColor color) {
        this.bandName = color;
    }

    public boolean getSpecial() {
        return special;
    }

    public void setSpecial(boolean special) {
        this.special = special;
    }

    public boolean getVisibility() {
        return visibility;
    }

    public void setVisibility(boolean visibility) {
        this.visibility = visibility;
    }

    public void setHotel(boolean hotel) {
        this.hotel = hotel;
    }

    public boolean getHotel() {
        return hotel;
    }

    public int getFastpass() {
        return fastpass;
    }

    public void setFastpass(int fastpass) {
        this.fastpass = fastpass;
    }
}
