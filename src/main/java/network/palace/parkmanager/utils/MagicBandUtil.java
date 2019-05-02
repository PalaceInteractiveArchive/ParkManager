package network.palace.parkmanager.utils;

import network.palace.core.utils.ItemUtil;
import network.palace.parkmanager.handlers.magicband.BandType;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkEffectMeta;

public class MagicBandUtil {

    public ItemStack getMagicBandItem(String type, String color) {
        BandType bandType = BandType.fromString(type);
        ItemStack item;
        if (bandType.isColor()) {
            item = ItemUtil.create(Material.FIREWORK_STAR, getNameColor(color) + "MagicBand " + ChatColor.GRAY + "(Right-Click)");
            FireworkEffectMeta meta = (FireworkEffectMeta) item.getItemMeta();
            meta.setEffect(FireworkEffect.builder().withColor(getBandColor(bandType)).build());
            item.setItemMeta(meta);
        } else {
            item = null;
        }
        return item;
    }

    private Color getBandColor(BandType type) {
        switch (type) {
            case ORANGE:
                return Color.fromRGB(247, 140, 0);
            case YELLOW:
                return Color.fromRGB(239, 247, 0);
            case GREEN:
                return Color.fromRGB(0, 192, 13);
            case BLUE:
                return Color.fromRGB(41, 106, 255);
            case PURPLE:
                return Color.fromRGB(176, 0, 220);
            case PINK:
                return Color.fromRGB(246, 120, 255);
            default:
                return Color.fromRGB(255, 40, 40);
        }
    }

    private ChatColor getNameColor(String color) {
        switch (color.toLowerCase()) {
            case "red":
                return ChatColor.RED;
            case "yellow":
                return ChatColor.YELLOW;
            case "green":
                return ChatColor.GREEN;
            case "darkgreen":
                return ChatColor.DARK_GREEN;
            case "blue":
                return ChatColor.BLUE;
            case "purple":
                return ChatColor.DARK_PURPLE;
            default:
                return ChatColor.GOLD;
        }
    }
}
