package network.palace.parkmanager.show.handlers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;

/**
 * Created by Marc on 10/29/15
 */
@Getter
@AllArgsConstructor
public enum ShowType {
    //Normal Shows
    TBA(ChatColor.LIGHT_PURPLE + "To Be Announced", Material.BARRIER),
    HEA(ChatColor.GOLD + "Happily Ever After", Material.GLOWSTONE_DUST),
    WISHES(ChatColor.AQUA + "Wishes", Material.BLAZE_ROD),
    IROE(ChatColor.GREEN + "Illuminations: Reflections of Earth", Material.NETHER_STAR),
    SITS(ChatColor.GOLD + "Symphony in the Stars", Material.DIAMOND_SWORD),
    SPECIAL(ChatColor.DARK_PURPLE + "Special Event", Material.DIAMOND),
    //Stage Shows
    FANTASMIC(ChatColor.BLUE + "Fantasmic", Material.DIAMOND_HELMET),
    FOTLK(ChatColor.YELLOW + "Festival of the Lion King", Material.INK_SACK, (byte) 3),
    FNTM(ChatColor.BLUE + "Finding Nemo: The Musical", Material.RAW_FISH, (byte) 2),
    JEDI(ChatColor.BLUE + "Jedi Training", Material.IRON_SWORD),
    MRFF(ChatColor.GOLD + "Mickey’s Royal Friendship Faire", Material.INK_SACK),
    SGE(ChatColor.BLUE + "Stitch's Great Escape", Material.INK_SACK, (byte) 6),
    //Parades
    FOF(ChatColor.DARK_AQUA + "Festival of Fantasy Parade", Material.INK_SACK, (byte) 12),
    MSEP(ChatColor.YELLOW + "Main Street Electrical Parade", Material.BLAZE_POWDER),
    MISIP(ChatColor.GREEN + "Move It Shake It Parade", Material.SUGAR),
    //Fourth of July
    CA(ChatColor.RED + "Celebrate " + ChatColor.BLUE + "America", Material.BANNER),
    //Halloween
    HALLOWISHES(ChatColor.GOLD + "Happy HalloWishes", Material.JACK_O_LANTERN),
    HOCUSPOCUS(ChatColor.GOLD + "Hocus Pocus Villain Spelltacular", Material.CAULDRON_ITEM),
    BOOTOYOU(ChatColor.GOLD + "Mickey's Boo To You Halloween Parade", Material.ROTTEN_FLESH),
    //Christmas
    FITS(ChatColor.BLUE + "Fantasy in the Sky", Material.DIAMOND),
    FHW(ChatColor.AQUA + "Frozen Holiday Wish", Material.QUARTZ),
    HOLIDAYWISHES(ChatColor.AQUA + "Holiday Wishes", Material.SNOW),
    OUACTP(ChatColor.AQUA + "Once Upon A Christmastime Parade", Material.SNOW_BALL),
    JBJB(ChatColor.GREEN + "Jingle Bell, Jingle BAM!", Material.RECORD_5),
    //Seasonal
    BITHM(ChatColor.AQUA + "Believe in the Holiday Magic", Material.BLAZE_ROD),
    //Anniversary
    MCMD(ChatColor.LIGHT_PURPLE + "Dreams", Material.GLOWSTONE_DUST),
    //DVC
    MOONLIGHT(ChatColor.AQUA + "Moonlight Magic", Material.BLUE_GLAZED_TERRACOTTA);

    private String name;
    private Material type;
    private byte data;

    ShowType(String name, Material type) {
        this(name, type, (byte) 0);
    }

    public static ShowType fromString(String name) {
        switch (name.toLowerCase()) {
            case "hea":
                return HEA;
            case "wishes":
                return WISHES;
            case "iroe":
                return IROE;
            case "sits":
                return SITS;
            case "special":
                return SPECIAL;
            case "fantasmic":
                return FANTASMIC;
            case "fotlk":
                return FOTLK;
            case "fntm":
                return FNTM;
            case "jedi":
                return JEDI;
            case "mrff":
                return MRFF;
            case "sge":
                return SGE;
            case "fof":
                return FOF;
            case "msep":
                return MSEP;
            case "misip":
                return MISIP;
            case "ca":
                return CA;
            case "hallowishes":
                return HALLOWISHES;
            case "hocuspocus":
                return HOCUSPOCUS;
            case "bootoyou":
                return BOOTOYOU;
            case "fits":
                return FITS;
            case "fhw":
                return FHW;
            case "holidaywishes":
                return HOLIDAYWISHES;
            case "ouactp":
                return OUACTP;
            case "jbjb":
                return JBJB;
            case "bithm":
                return BITHM;
            case "mcmd":
                return MCMD;
            case "moonlight":
                return MOONLIGHT;
        }
        return TBA;
    }
}
