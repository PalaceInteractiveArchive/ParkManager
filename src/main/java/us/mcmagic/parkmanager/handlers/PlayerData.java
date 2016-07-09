package us.mcmagic.parkmanager.handlers;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import us.mcmagic.parkmanager.ParkManager;
import us.mcmagic.parkmanager.storage.Backpack;
import us.mcmagic.parkmanager.storage.Locker;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
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
    private boolean loop;
    private boolean hotel;
    private FastPassData fastPassData;
    private KioskData kioskData;
    private Backpack backpack;
    private Locker locker;
    private TreeMap<String, RideCount> rideCounts = new TreeMap<>();
    private List<Integer> purchases;
    private Clothing clothing;
    private String outfitCode;
    private boolean inventorySet = false;

    public PlayerData(UUID uuid, boolean dvc, ChatColor bandName, BandColor bandColor, boolean special, boolean flash,
                      boolean visibility, boolean loop, boolean hotel, FastPassData fastPassData, KioskData kioskData,
                      String outfitCode) {
        this.uuid = uuid;
        this.dvc = dvc;
        this.bandName = bandName;
        this.bandColor = bandColor;
        this.friends = friends;
        this.special = special;
        this.flash = flash;
        this.visibility = visibility;
        this.loop = loop;
        this.hotel = hotel;
        this.fastPassData = fastPassData;
        this.kioskData = kioskData;
        this.purchases = purchases;
        this.outfitCode = outfitCode;
        Clothing c = new Clothing();
        String[] list = outfitCode.split(",");
        int in = 0;
        for (String s : list) {
            try {
                Integer i = Integer.parseInt(s);
                Outfit o = ParkManager.wardrobeManager.getOutfit(i);
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

    public boolean getLoop() {
        return loop;
    }

    public Backpack getBackpack() {
        return backpack;
    }

    public void setBackpack(Backpack backpack) {
        this.backpack = backpack;
    }

    public void setLoop(boolean loop) {
        this.loop = loop;
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

    public void addPurchase(Integer id) {
        purchases.add(id);
    }

    public TreeMap<String, RideCount> getRideCounts() {
        return new TreeMap<>(rideCounts);
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

    public FastPassData getFastPassData() {
        return fastPassData;
    }

    public KioskData getKioskData() {
        return kioskData;
    }

    public void setRideCounts(TreeMap<String, RideCount> rideCounts) {
        this.rideCounts = rideCounts;
    }

    public Locker getLocker() {
        return locker;
    }

    public void setLocker(Locker locker) {
        this.locker = locker;
    }

    public List<Integer> getPurchases() {
        return new ArrayList<>(purchases);
    }

    public void setPurchases(List<Integer> purchases) {
        this.purchases = purchases;
    }

    public void addPurchase(int i) {
        if (!purchases.contains(i)) {
            purchases.add(i);
        }
    }

    public String getOutfitCode() {
        return outfitCode;
    }

    public void setOutfitCode(String outfitCode) {
        this.outfitCode = outfitCode;
    }

    public Clothing getClothing() {
        return clothing;
    }

    public void setClothing(Clothing clothing) {
        this.clothing = clothing;
    }

    public void setFriendList(List<UUID> friendList) {
        this.friends = friendList;
    }

    public class Clothing {
        private ItemStack head = null;
        private int headID;
        private ItemStack shirt = null;
        private int shirtID;
        private ItemStack pants = null;
        private int pantsID;
        private ItemStack boots = null;
        private int bootsID;

        public Clothing() {
        }

        public Clothing(ItemStack head, int headID, ItemStack shirt, int shirtID, ItemStack pants,
                        int pantsID, ItemStack boots, int bootsID) {
            this.head = head;
            this.headID = headID;
            this.shirt = shirt;
            this.shirtID = shirtID;
            this.pants = pants;
            this.pantsID = pantsID;
            this.boots = boots;
            this.bootsID = bootsID;
        }

        public ItemStack getHead() {
            return head;
        }

        public int getHeadID() {
            return headID;
        }

        public ItemStack getShirt() {
            return shirt;
        }

        public int getShirtID() {
            return shirtID;
        }

        public ItemStack getPants() {
            return pants;
        }

        public int getPantsID() {
            return pantsID;
        }

        public ItemStack getBoots() {
            return boots;
        }

        public int getBootsID() {
            return bootsID;
        }

        public void setHead(ItemStack head) {
            this.head = head;
        }

        public void setHeadID(int headID) {
            this.headID = headID;
        }

        public void setShirt(ItemStack shirt) {
            this.shirt = shirt;
        }

        public void setShirtID(int shirtID) {
            this.shirtID = shirtID;
        }

        public void setPants(ItemStack pants) {
            this.pants = pants;
        }

        public void setPantsID(int pantsID) {
            this.pantsID = pantsID;
        }

        public void setBoots(ItemStack boots) {
            this.boots = boots;
        }

        public void setBootsID(int bootsID) {
            this.bootsID = bootsID;
        }

        public void setInventorySet(boolean b) {
            inventorySet = b;
        }

        public boolean getInventorySet() {
            return inventorySet;
        }
    }
}