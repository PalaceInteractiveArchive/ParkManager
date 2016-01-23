package us.mcmagic.parkmanager.shop;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import us.mcmagic.mcmagiccore.itemcreator.ItemCreator;

/**
 * Created by Marc on 5/29/15
 */
public enum ShopCategory {
    WARDROBE(ChatColor.BLUE + "Wardrobe", new ItemCreator(Material.DIAMOND_HELMET, ChatColor.BLUE + "Wardrobe")),
    TOYS(ChatColor.GREEN + "Toys", new ItemCreator(Material.STONE_SWORD, ChatColor.GREEN + "Toys")),
    DOLLS(ChatColor.YELLOW + "Dolls", new ItemCreator(Material.INK_SACK, ChatColor.YELLOW + "Dolls"));

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
