package us.mcmagic.parkmanager.uso.rrr;

import org.bukkit.inventory.ItemStack;

/**
 * Created by Marc on 4/13/17.
 */
public class Song {
    private String name;
    private String title;
    private String area;
    private ItemStack item;

    public Song(String name, String area, String title, ItemStack item) {
        this.name = name;
        this.area = area;
        this.title = title;
        this.item = item;
    }

    public String getName() {
        return name;
    }

    public String getArea() {
        return area;
    }

    public String getTitle() {
        return title;
    }

    public ItemStack getItem() {
        return item;
    }
}
