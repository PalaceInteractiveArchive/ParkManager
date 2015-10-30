package us.mcmagic.magicassistant.show.handlers.schedule;

import org.bukkit.ChatColor;
import org.bukkit.Material;

/**
 * Created by Marc on 10/29/15
 */
public enum ShowType {
    RANDOM(ChatColor.LIGHT_PURPLE + "Random", Material.BARRIER),
    WISHES(ChatColor.AQUA + "Wishes", Material.BLAZE_ROD),
    IROE(ChatColor.GREEN + "IROE", Material.MONSTER_EGG),
    HALLOWISHES(ChatColor.GOLD + "HalloWishes", Material.JACK_O_LANTERN),
    HOLIDAYWISHES(ChatColor.AQUA + "Holiday Wishes", Material.SNOW),
    FANTASMIC(ChatColor.BLUE + "Fantasmic", Material.DIAMOND_HELMET),
    MSEP(ChatColor.YELLOW + "Main Street Electrical Parade", Material.GLOWSTONE_DUST),
    FOF(ChatColor.DARK_AQUA + "Festival of Fantasy Parade", Material.INK_SACK, (byte) 12),
    FNTM(ChatColor.BLUE + "Finding Nemo: The Musical", Material.RAW_FISH, (byte) 2),
    MISIP(ChatColor.GREEN + "Move It Shake It Parade", Material.DIAMOND),
    SPECIAL(ChatColor.DARK_PURPLE + "Special Event", Material.NETHER_STAR),
    CA(ChatColor.RED + "Celebrate " + ChatColor.BLUE + "America", Material.BANNER),
    FITS(ChatColor.BLUE + "Fantasy in the Sky", Material.NETHER_STAR),
    DAWM(ChatColor.GREEN + "Dream Along with Mickey", Material.INK_SACK),
    JEDI(ChatColor.BLUE + "Jedi Training", Material.IRON_SWORD),
    OSBORNE(ChatColor.AQUA + "The Osborne Family Spectacle of Dancing Lights", Material.GLOWSTONE),
    FOTLK(ChatColor.YELLOW + "Festival of the Lion King", Material.INK_SACK, (byte) 3);

    private String name;
    private Material type;
    private byte data;

    ShowType(String name, Material type) {
        this(name, type, (byte) 0);
    }

    ShowType(String name, Material type, byte data) {
        this.name = name;
        this.type = type;
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public Material getType() {
        return type;
    }

    public byte getData() {
        return data;
    }

    public static ShowType fromString(String name) {
        switch (name.toLowerCase()) {
            case "wishes":
                return WISHES;
            case "random":
                return RANDOM;
            case "iroe":
                return IROE;
            case "halloween":
                return HALLOWISHES;
            case "holidayw":
                return HOLIDAYWISHES;
            case "fant":
                return FANTASMIC;
            case "msep":
                return MSEP;
            case "fof":
                return FOF;
            case "fntm":
                return FNTM;
            case "misip":
                return MISIP;
            case "special":
                return SPECIAL;
            case "ca":
                return CA;
            case "fits":
                return FITS;
            case "dawm":
                return DAWM;
            case "jedi":
                return JEDI;
            case "ofl":
                return OSBORNE;
            case "fotlk":
                return FOTLK;
        }
        return WISHES;
    }
}