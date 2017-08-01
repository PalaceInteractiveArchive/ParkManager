package network.palace.parkmanager.handlers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.storage.Backpack;
import network.palace.parkmanager.storage.Locker;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.UUID;

/**
 * Created by Marc on 12/13/14
 */
public class PlayerData {
    @Getter private UUID uuid;
    @Getter @Setter private ChatColor bandName;
    @Getter @Setter private BandColor bandColor;
    private @Setter List<UUID> friends;
    @Getter @Setter private boolean special;
    @Getter @Setter private boolean flash;
    @Getter @Setter private boolean visibility;
    @Getter @Setter private boolean loop;
    @Getter @Setter private boolean hotel;
    @Getter private FastPassData fastPassData;
    @Getter private KioskData kioskData;
    @Getter @Setter private Backpack backpack;
    @Getter @Setter private Locker locker;
    private @Setter TreeMap<String, RideCount> rideCounts = new TreeMap<>();
    private @Setter List<Integer> purchases;
    @Getter @Setter private Clothing clothing;
    @Getter @Setter private String outfitCode;
    @Getter @Setter private String pack;
    @Getter @Setter private long lastInventoryUpdate = System.currentTimeMillis();
    @Getter @Setter private String backpackHash = "";
    @Getter @Setter private String lockerHash = "";
    @Getter @Setter private String hotbarHash = "";

    public PlayerData(UUID uuid, ChatColor bandName, BandColor bandColor, boolean special, boolean flash,
                      boolean visibility, boolean loop, boolean hotel, FastPassData fastPassData, KioskData kioskData,
                      String outfitCode, String pack) {
        this.uuid = uuid;
        this.bandName = bandName;
        this.bandColor = bandColor;
        this.special = special;
        this.flash = flash;
        this.visibility = visibility;
        this.loop = loop;
        this.hotel = hotel;
        this.fastPassData = fastPassData;
        this.kioskData = kioskData;
        this.outfitCode = outfitCode;
        this.pack = pack;
        Clothing c = new Clothing();
        String[] list = outfitCode.split(",");
        int in = 0;
        for (String s : list) {
            try {
                Integer i = Integer.parseInt(s);
                Outfit o = ParkManager.getInstance().getWardrobeManager().getOutfit(i);
                if (o == null) {
                    continue;
                }
                switch (in) {
                    case 0:
                        c.setHead(o.getHead());
                        c.setHeadID(i);
                        break;
                    case 1:
                        c.setShirt(o.getShirt());
                        c.setShirtID(i);
                        break;
                    case 2:
                        c.setPants(o.getPants());
                        c.setPantsID(i);
                        break;
                    case 3:
                        c.setBoots(o.getBoots());
                        c.setBootsID(i);
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            in++;
        }
        this.clothing = c;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public List<UUID> getFriendList() {
        return new ArrayList<>(friends);
    }

    public void setBandColorMaterial(Material color) {
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

    public void addPurchase(Integer id) {
        purchases.add(id);
    }

    public TreeMap<String, RideCount> getRideCounts() {
        return new TreeMap<>(rideCounts);
    }

    public List<Integer> getPurchases() {
        return new ArrayList<>(purchases);
    }

    public void addPurchase(int i) {
        if (!purchases.contains(i)) {
            purchases.add(i);
        }
    }

    @AllArgsConstructor
    @NoArgsConstructor
    public class Clothing {
        @Getter @Setter private ItemStack head = null;
        @Getter @Setter private int headID;
        @Getter @Setter private ItemStack shirt = null;
        @Getter @Setter private int shirtID;
        @Getter @Setter private ItemStack pants = null;
        @Getter @Setter private int pantsID;
        @Getter @Setter private ItemStack boots = null;
        @Getter @Setter private int bootsID;
    }
}