package us.mcmagic.magicassistant;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import us.mcmagic.magicassistant.magicband.BandColor;
import us.mcmagic.mcmagiccore.permissions.Rank;
import us.mcmagic.mcmagiccore.player.PlayerUtil;

import java.util.HashMap;
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
    private HashMap<UUID, String> friends = new HashMap<>();
    private HashMap<Integer, List<String>> pages = new HashMap<>();
    private boolean special;

    public PlayerData(UUID uuid, ChatColor bandName, BandColor bandColor, HashMap<UUID, String> friends, HashMap<Integer, List<String>> pages, boolean special) {
        this.uuid = uuid;
        dvc = PlayerUtil.getUser(uuid).getRank().equals(Rank.DVCMEMBER);
        this.bandName = bandName;
        this.bandColor = bandColor;
        this.friends = friends;
        this.pages = pages;
        this.special = special;
    }

    public UUID getUuid() {
        return uuid;
    }

    public boolean getDVC() {
        return dvc;
    }

    public ChatColor getBandName() {
        return bandName;
    }

    public BandColor getBandColor() {
        return bandColor;
    }

    public HashMap<UUID, String> getFriendList() {
        return friends;
    }

    public HashMap<Integer, List<String>> getPages() {
        return pages;
    }

    public void setBandColor(BandColor color) {
        this.bandColor = color;
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
}
