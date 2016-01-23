package us.mcmagic.parkmanager.handlers;

import org.bukkit.inventory.ItemStack;

/**
 * Created by Marc on 11/9/15
 */
public class Outfit {
    private Integer id;
    private String name;
    private ItemStack head;
    private ItemStack shirt;
    private ItemStack pants;
    private ItemStack boots;

    public Outfit(Integer id, String name, ItemStack head, ItemStack shirt, ItemStack pants, ItemStack boots) {
        this.id = id;
        this.name = name;
        this.head = head;
        this.shirt = shirt;
        this.pants = pants;
        this.boots = boots;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ItemStack getHead() {
        return head;
    }

    public ItemStack getShirt() {
        return shirt;
    }

    public ItemStack getPants() {
        return pants;
    }

    public ItemStack getBoots() {
        return boots;
    }
}