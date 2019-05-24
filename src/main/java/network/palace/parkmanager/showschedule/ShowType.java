package network.palace.parkmanager.showschedule;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;

@Getter
@AllArgsConstructor
public enum ShowType {
    NO_SHOW(ChatColor.GRAY + "No Show Scheduled", Material.GRAY_STAINED_GLASS_PANE),
    TBA(ChatColor.LIGHT_PURPLE + "To Be Announced", Material.BARRIER),
    //Normal Shows
    HEA(ChatColor.YELLOW + "Happily Ever After", Material.GLOWSTONE_DUST),
    WISHES(ChatColor.AQUA + "Wishes", Material.BLAZE_ROD),
    IROE(ChatColor.GREEN + "Illuminations: Reflections of Earth", Material.NETHER_STAR),
    SITS(ChatColor.GOLD + "Symphony in the Stars", Material.DIAMOND_SWORD),
    SPECIAL(ChatColor.DARK_PURPLE + "Special Event", Material.DIAMOND),
    //Stage Shows
    FANTASMIC(ChatColor.BLUE + "Fantasmic", Material.DIAMOND_HELMET),
    FOTLK(ChatColor.YELLOW + "Festival of the Lion King", Material.COCOA_BEANS),
    FNTM(ChatColor.BLUE + "Finding Nemo: The Musical", Material.TROPICAL_FISH),
    JEDI(ChatColor.BLUE + "Jedi Training", Material.IRON_SWORD),
    MRFF(ChatColor.GOLD + "Mickey’s Royal Friendship Faire", Material.INK_SAC),
    SGE(ChatColor.BLUE + "Stitch's Great Escape", Material.CYAN_DYE),
    //Parades
    FOF(ChatColor.DARK_AQUA + "Festival of Fantasy Parade", Material.LIGHT_BLUE_DYE),
    MSEP(ChatColor.YELLOW + "Main Street Electrical Parade", Material.BLAZE_POWDER),
    MISIP(ChatColor.GREEN + "Move It Shake It Parade", Material.SUGAR),
    //Fourth of July
    CA(ChatColor.RED + "Celebrate " + ChatColor.BLUE + "America", Material.WHITE_BANNER),
    //Halloween
    HALLOWISHES(ChatColor.GOLD + "Happy HalloWishes", Material.JACK_O_LANTERN),
    HOCUSPOCUS(ChatColor.GOLD + "Hocus Pocus Villain Spelltacular", Material.CAULDRON),
    BOOTOYOU(ChatColor.GOLD + "Mickey's Boo To You Halloween Parade", Material.ROTTEN_FLESH),
    //Christmas
    FITS(ChatColor.BLUE + "Fantasy in the Sky", Material.DIAMOND),
    FHW(ChatColor.AQUA + "Frozen Holiday Wish", Material.QUARTZ),
    HOLIDAYWISHES(ChatColor.AQUA + "Holiday Wishes", Material.SNOW),
    OUACTP(ChatColor.AQUA + "Once Upon A Christmastime Parade", Material.SNOWBALL),
    JBJB(ChatColor.GREEN + "Jingle Bell, Jingle BAM!", Material.MUSIC_DISC_FAR),
    //Seasonal
    BITHM(ChatColor.AQUA + "Believe in the Holiday Magic", Material.BLAZE_ROD),
    //Anniversary
    MCMD(ChatColor.LIGHT_PURPLE + "Dreams", Material.GLOWSTONE_DUST);

    private String name;
    private Material type;

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
