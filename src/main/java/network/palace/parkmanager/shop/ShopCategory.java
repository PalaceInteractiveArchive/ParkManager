package network.palace.parkmanager.shop;

import network.palace.core.utils.ItemUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Marc on 5/29/15
 */
public enum ShopCategory {
    WARDROBE(ChatColor.BLUE + "Wardrobe", ItemUtil.create(Material.DIAMOND_HELMET, ChatColor.BLUE + "Wardrobe")),
    TOYS(ChatColor.GREEN + "Toys", ItemUtil.create(Material.STONE_SWORD, ChatColor.GREEN + "Toys")),
    DOLLS(ChatColor.YELLOW + "Dolls", ItemUtil.create(Material.INK_SACK, ChatColor.YELLOW + "Dolls"));

    String name;
    ItemStack stack;

    ShopCategory(String name, ItemStack stack) {
        this.name = name;
        this.stack = stack;
    }

    public String getName() {
        return name;
    }

    public ItemStack getStack() {
        return stack;
    }

    public static ShopCategory fromString(String s) {
        switch (s.toLowerCase()) {
            case "wardrobe":
                return WARDROBE;
            case "toys":
                return TOYS;
            case "dolls":
                return DOLLS;
        }
        return WARDROBE;
    }
}
