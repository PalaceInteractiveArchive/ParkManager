package us.mcmagic.parkmanager.bb8;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

// Author: BeMacized
// http://models.bemacized.net/

@SuppressWarnings("deprecation")
public class CustomIS {
    private String name;
    private final ArrayList<String> lore;
    private Material material;
    private byte data;
    private int size;
    private HashMap<Enchantment, Integer> enchantments;
    private ArrayList<ItemFlag> flags;
    private JSONObject meta;

    public CustomIS() {
        this.name = "";
        this.lore = new ArrayList<>();
        this.material = Material.DIAMOND;
        this.data = 0;
        this.size = 1;
        this.enchantments = new HashMap<>();
        this.flags = new ArrayList<>();
        this.meta = new JSONObject();
    }

    public CustomIS(ItemStack is) {
        this.name = is.getType().name();
        this.material = is.getType();
        this.data = (byte) is.getDurability();
        this.size = is.getAmount();
        this.enchantments = (HashMap<Enchantment, Integer>) is.getEnchantments();
        this.flags = new ArrayList<>();
        this.meta = new JSONObject();
        JSONObject c = CustomIS.getMeta(is);
        if (c != null) {
            meta = c;
        }
        ItemMeta meta = is.getItemMeta();
        if (meta == null) {
            this.lore = new ArrayList<>();
            return;
        }
        this.flags.addAll(meta.getItemFlags().stream().collect(Collectors.toList()));
        if (meta.hasDisplayName()) {
            this.name = meta.getDisplayName();
        }
        if (meta.hasLore()) {
            this.lore = (ArrayList<String>) meta.getLore();
        } else {
            this.lore = new ArrayList<>();
        }
    }

//    public CustomIS(BasicDBObject dbo) {
//        this.name = "";
//        this.lore = new ArrayList<>();
//        this.material = Material.DIAMOND;
//        this.data = 0;
//        this.size = 1;
//        this.enchantments = new HashMap<>();
//        this.flags = new ArrayList<>();
//        if (dbo.containsKey("name")) {
//            setName(ChatColor.translateAlternateColorCodes('&', dbo.getString("name")));
//        }
//        if (dbo.containsKey("typeid")) {
//            setMaterial(Material.getMaterial(dbo.getInt("typeid")));
//        }
//        if (dbo.containsKey("data")) {
//            setData((byte) dbo.getInt("data"));
//        }
//        if (dbo.containsKey("amount")) {
//            setSize(dbo.getInt("amount"));
//        }
//        if (dbo.containsKey("lore")) {
//            for (Object loredbo : (BasicDBList) dbo.get("lore")) {
//                addLore(ChatColor.translateAlternateColorCodes('&', (String) loredbo));
//            }
//        }
//        if (dbo.containsKey("enchantments")) {
//            for (Object edbo : (BasicDBList) dbo.get("enchantments")) {
//                addEnchantment(Enchantment.getByName(((BasicDBObject) edbo).getString("name").toUpperCase()), ((BasicDBObject) edbo).getInt("level"));
//            }
//        }
//        if (dbo.containsKey("flags")) {
//            for (Object flagdbo : (BasicDBList) dbo.get("flags")) {
//                addItemFlag(ItemFlag.valueOf(((String) flagdbo).toUpperCase()));
//            }
//        }
//    }

    public CustomIS clone() {
        CustomIS cis = new CustomIS();
        cis.setName(name);
        cis.setMaterial(material);
        cis.setData(data);
        cis.setSize(size);
        lore.forEach(cis::addLore);
        for (Enchantment e : enchantments.keySet()) {
            cis.addEnchantment(e, enchantments.get(e));
        }
        flags.forEach(cis::addItemFlag);
        return cis;
    }

    public CustomIS addItemFlag(ItemFlag _if) {
        if (!flags.contains(_if)) {
            flags.add(_if);
        }
        return this;
    }

    public CustomIS addEnchantment(Enchantment e, int level) {
        enchantments.put(e, level);
        return this;
    }

    public boolean equals(ItemStack is) {
        return !(is.getType() != material || is.getData().getData() != data || (!is.hasItemMeta() && (!lore.isEmpty() || !name.isEmpty())) || (is.hasItemMeta() && (!is.getItemMeta().getDisplayName().equals(name) || !lore.equals(is.getItemMeta().getLore()))));
    }

    public CustomIS setName(String name) {
        this.name = name;
        return this;
    }

    public CustomIS addLore(String line) {
        lore.add(line);
        return this;
    }

    public CustomIS setMaterial(Material mat) {
        material = mat;
        return this;
    }

    public CustomIS setData(byte data) {
        this.data = data;
        return this;
    }

    public CustomIS setSize(int size) {
        this.size = size;
        return this;
    }

    public CustomIS setMeta(JSONObject meta) {
        this.meta = meta;
        return this;
    }

    public CustomIS glow() {
        addEnchantment(new ItemGlow(420), 1);
        return this;
    }

    public JSONObject getMeta() {
        return meta;
    }

    public String getName() {
        return name;
    }

    public Material getMaterial() {
        return material;
    }

    public byte getData() {
        return data;
    }

    public int getSize() {
        return size;
    }

    public ArrayList<String> getLore() {
        return lore;
    }

    public HashMap<Enchantment, Integer> getEnchantments() {
        return enchantments;
    }

    public ArrayList<ItemFlag> getFlags() {
        return flags;
    }

    public ItemStack get() {
        ItemStack is = new ItemStack(material, size);
        is.setDurability((short) data);
        ItemMeta im = is.getItemMeta();
        if (!name.isEmpty()) {
            im.setDisplayName(name);
        }
        flags.forEach(im::addItemFlags);
        ArrayList<String> lore_wmeta = (ArrayList<String>) lore.clone();
        //lore_wmeta.add(meta.toJSONString());
        im.setLore(lore_wmeta);
        is.setItemMeta(im);
        is.addUnsafeEnchantments(enchantments);
        return is;
    }

    public static JSONObject getMeta(ItemStack is) {
        if (is == null) return null;
        if (!is.hasItemMeta()) return null;
        ItemMeta im = is.getItemMeta();
        if (!im.hasLore()) {
            return null;
        }
        for (String s : im.getLore()) {
            try {
                return (JSONObject) (new JSONParser()).parse(s);
            } catch (Exception e) {
                //e.printStackTrace();
            }
        }
        return null;
    }
}