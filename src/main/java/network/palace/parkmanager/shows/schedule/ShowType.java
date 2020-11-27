package network.palace.parkmanager.shows.schedule;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;

@Getter
@AllArgsConstructor
public enum ShowType {
    NO_SHOW(ChatColor.GRAY + "No Show Scheduled", Material.STAINED_GLASS_PANE, (byte) 7),
    TBA(ChatColor.LIGHT_PURPLE + "To Be Announced", Material.BARRIER),
    //Normal Shows
    HEA(ChatColor.YELLOW + "Happily Ever After", Material.GLOWSTONE_DUST),
    WISHES(ChatColor.AQUA + "Wishes", Material.BLAZE_ROD),
    EPCOT(ChatColor.DARK_PURPLE + "Epcot Forever", Material.SNOW_BALL),
    IROE(ChatColor.GREEN + "Illuminations: Reflections of Earth", Material.NETHER_STAR),
    SITS(ChatColor.GOLD + "Symphony in the Stars", Material.DIAMOND_SWORD),
    SPECIAL(ChatColor.DARK_PURPLE + "Special Event", Material.DIAMOND),
    //Projection Shows
    OUAT(ChatColor.YELLOW + "Once Upon A Time", Material.BOOK),
    //Stage Shows
    FANTASMIC(ChatColor.BLUE + "Fantasmic", Material.DIAMOND_HELMET),
    FOTLK(ChatColor.YELLOW + "Festival of the Lion King", Material.INK_SACK, (byte) 3),
    FNTM(ChatColor.BLUE + "Finding Nemo: The Musical", Material.RAW_FISH, (byte) 2),
    JEDI(ChatColor.BLUE + "Jedi Training", Material.IRON_SWORD),
    MRFF(ChatColor.GOLD + "Mickey’s Royal Friendship Faire", Material.INK_SACK),
    GRINCHMAS(ChatColor.GREEN + "Grinchmas Wholiday Spectacular", Material.QUARTZ),
    //Parades
    FOF(ChatColor.DARK_AQUA + "Festival of Fantasy Parade", Material.INK_SACK, (byte) 12),
    MSEP(ChatColor.YELLOW + "Main Street Electrical Parade", Material.BLAZE_POWDER),
    MISIP(ChatColor.GREEN + "Move It! Shake It! MousekeDance It!", Material.SUGAR),
    MAGICHAPPENS(ChatColor.DARK_GREEN + "Magic Happens", Material.END_CRYSTAL),
    //Fourth of July
    CA(ChatColor.RED + "Celebrate " + ChatColor.BLUE + "America", Material.BANNER),
    //Halloween
    HALLOWISHES(ChatColor.GOLD + "Happy HalloWishes", Material.JACK_O_LANTERN),
    NOT_SO_SPOOKY(ChatColor.GOLD + "Not So Spooky Spectacular", Material.JACK_O_LANTERN),
    HOCUSPOCUS(ChatColor.GOLD + "Hocus Pocus Villain Spelltacular", Material.CAULDRON_ITEM),
    BOOTOYOU(ChatColor.GOLD + "Mickey's Boo To You Halloween Parade", Material.ROTTEN_FLESH),
    //Christmas
    FITS(ChatColor.BLUE + "Fantasy in the Sky", Material.DIAMOND),
    FHW(ChatColor.AQUA + "Frozen Holiday Wish", Material.QUARTZ),
    HOLIDAYWISHES(ChatColor.AQUA + "Holiday Wishes", Material.SNOW),
    CHRISTMASTIME_FIREWORKS(ChatColor.LIGHT_PURPLE + "Minnie's Wonderful Christmastime Fireworks", Material.SNOW),
    MERRIEST_CELEBRATION(ChatColor.RED + "Mickey's Most Merriest Celebration", Material.MELON),
    OUACTP(ChatColor.AQUA + "Once Upon A Christmastime Parade", Material.SNOW_BALL),
    JBJB(ChatColor.GREEN + "Jingle Bell, Jingle BAM!", Material.RECORD_5);

    private String name;
    private Material type;
    private byte data;

    ShowType(String name, Material type) {
        this.name = name;
        this.type = type;
        this.data = 0;
    }

    public static ShowType fromString(String name) {
        for (ShowType type : values()) {
            if (name.equalsIgnoreCase(type.getDBName())) {
                return type;
            }
        }
        return NO_SHOW;
    }

    public String getDBName() {
        return name().toLowerCase().replaceAll("_", "");
    }
}
