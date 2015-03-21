package us.mcmagic.magicassistant.handlers;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

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

    public PlayerData(UUID uuid, boolean dvc, ChatColor bandName, BandColor bandColor, HashMap<UUID, String> friends, HashMap<Integer, List<String>> pages, boolean special) {
        this.uuid = uuid;
        this.dvc = dvc;
        this.bandName = bandName;
        this.bandColor = bandColor;
        this.friends = friends;
        this.pages = pages;
        this.special = special;
    }

    public UUID getUuid() {
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

    /**
     * Created by Marc on 12/22/14
     */
    @SuppressWarnings("deprecation")
    public static class Attraction {
        private String displayName;
        private String warp;
        private int id;
        private byte data;

        public Attraction(String displayName, String warp, int id, byte data) {
            this.displayName = displayName;
            this.warp = warp;
            this.id = id;
            this.data = data;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getWarp() {
            return warp;
        }

        public int getId() {
            return id;
        }

        public byte getData() {
            return data;
        }

        public ItemStack getItem() {
            return new ItemStack(id, 1, data);
        }
    }

    /**
     * Created by Marc on 12/13/14
     */
    public static enum BandColor {
        RED("red"), ORANGE("orange"), YELLOW("yellow"), GREEN("green"), BLUE("blue"), PURPLE("purple"), PINK("pink"), SPECIAL1("s1"), SPECIAL2("s2"), SPECIAL3("s3"), SPECIAL4("s4"), SPECIAL5("s5");
        String name;

        BandColor(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public BandColor fromString(String s) {
            switch (s) {
                case "red":
                    return RED;
                case "orange":
                    return ORANGE;
                case "yellow":
                    return YELLOW;
                case "green":
                    return GREEN;
                case "blue":
                    return BLUE;
                case "purple":
                    return PURPLE;
                case "pink":
                    return PINK;
                case "s1":
                    return SPECIAL1;
                case "s2":
                    return SPECIAL2;
                case "s3":
                    return SPECIAL3;
                case "s4":
                    return SPECIAL4;
                case "s5":
                    return SPECIAL5;
                default:
                    return BLUE;
            }
        }
    }
}
