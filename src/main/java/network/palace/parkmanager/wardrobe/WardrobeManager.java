package network.palace.parkmanager.wardrobe;

import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.wrappers.nbt.NbtFactory;
import com.comphenix.protocol.wrappers.nbt.io.NbtTextSerializer;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import network.palace.core.Core;
import network.palace.core.menu.Menu;
import network.palace.core.menu.MenuButton;
import network.palace.core.player.CPlayer;
import network.palace.core.utils.ItemUtil;
import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.handlers.outfits.Outfit;
import network.palace.parkmanager.magicband.BandInventory;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class WardrobeManager {
    private HashMap<Integer, Outfit> outfits = new HashMap<>();

    public WardrobeManager() {
        initialize();
    }

    public void initialize() {
        outfits.clear();
        boolean convert = false;
        for (Document doc : Core.getMongoHandler().getOutfits()) {
            if (!doc.containsKey("shirt")) {
                convert = true;
                break;
            }
            outfits.put(doc.getInteger("id"), new Outfit(doc.getInteger("id"),
                    ChatColor.translateAlternateColorCodes('&', doc.getString("name")),
                    ItemUtil.getItemFromJsonNew(doc.getString("head")),
                    ItemUtil.getItemFromJsonNew(doc.getString("shirt")),
                    ItemUtil.getItemFromJsonNew(doc.getString("pants")),
                    ItemUtil.getItemFromJsonNew(doc.getString("boots"))));
        }
        if (convert) {
            outfits.clear();
            for (Document doc : Core.getMongoHandler().getOutfits()) {
                Outfit outfit = getLegacyOutfit(doc);
                outfits.put(outfit.getId(), outfit);
            }
            MongoCollection<Document> outfitsCollection = Core.getMongoHandler().getDatabase().getCollection("outfits");
            for (Outfit outfit : outfits.values()) {
                outfitsCollection.findOneAndUpdate(Filters.eq("id", outfit.getId()),
                        new Document("$set", new Document("head", ItemUtil.getJsonFromItemNew(outfit.getHead()))
                                .append("shirt", ItemUtil.getJsonFromItemNew(outfit.getShirt()))
                                .append("pants", ItemUtil.getJsonFromItemNew(outfit.getPants()))
                                .append("boots", ItemUtil.getJsonFromItemNew(outfit.getBoots()))
                        )
                );
            }
        }
    }

    public void openWardrobePage(CPlayer player, int page) {

        List<MenuButton> buttons = Arrays.asList(
                new MenuButton(13, ItemUtil.create(Material.REDSTONE_BLOCK, ChatColor.AQUA + "Pardon Our Pixie Dust!",
                        Arrays.asList(ChatColor.GRAY + "We've temporarily disabled outfits",
                                ChatColor.GRAY + "while we work to improve them",
                                ChatColor.GRAY + "behind the scenes.", "",
                                ChatColor.GRAY + "We apologize for the inconvenience,",
                                ChatColor.GRAY + "they will be returning shortly!"))),
                ParkManager.getMagicBandManager().getBackButton(22, BandInventory.MAIN)
        );

        new Menu(54, ChatColor.BLUE + "Wardrobe Manager Page " + page, player, buttons).open();
    }

    private Outfit getLegacyOutfit(Document doc) {
        try {
            int id = doc.getInteger("id");
            String ht = doc.getString("head");
            String ct = doc.getString("chest");
            String lt = doc.getString("leggings");
            String bt = doc.getString("boots");
            ItemStack h = MinecraftReflection.getBukkitItemStack(
                    new ItemStack(Material.getMaterial(doc.getInteger("headID")),
                            1,
                            (short) doc.getInteger("headData", 0))
            );
            if (!ht.equals("")) {
                NbtFactory.setItemTag(h, new NbtTextSerializer().deserializeCompound(ht));
            }
            ItemStack s = MinecraftReflection.getBukkitItemStack(
                    new ItemStack(Material.getMaterial(doc.getInteger("chestID")),
                            1,
                            (short) doc.getInteger("chestData", 0))
            );
            if (!ct.equals("")) {
                NbtFactory.setItemTag(s, new NbtTextSerializer().deserializeCompound(ct));
            }
            ItemStack l = MinecraftReflection.getBukkitItemStack(
                    new ItemStack(Material.getMaterial(doc.getInteger("leggingsID")),
                            1,
                            (short) doc.getInteger("leggingsData", 0))
            );
            if (!lt.equals("")) {
                NbtFactory.setItemTag(l, new NbtTextSerializer().deserializeCompound(lt));
            }
            ItemStack b = MinecraftReflection.getBukkitItemStack(
                    new ItemStack(Material.getMaterial(doc.getInteger("bootsID")),
                            1,
                            (short) doc.getInteger("bootsData", 0))
            );
            if (!bt.equals("")) {
                NbtFactory.setItemTag(b, new NbtTextSerializer().deserializeCompound(bt));
            }
            String name = ChatColor.translateAlternateColorCodes('&', doc.getString("name"));
            ItemMeta hm = h.getItemMeta();
            hm.setDisplayName(name + " Head");
            h.setItemMeta(hm);
            ItemMeta shm = s.getItemMeta();
            shm.setDisplayName(name + " Shirt");
            s.setItemMeta(shm);
            ItemMeta pm = l.getItemMeta();
            pm.setDisplayName(name + " Pants");
            l.setItemMeta(pm);
            ItemMeta bm = b.getItemMeta();
            bm.setDisplayName(name + " Boots");
            b.setItemMeta(bm);
            return new Outfit(id, name, h, s, l, b);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
