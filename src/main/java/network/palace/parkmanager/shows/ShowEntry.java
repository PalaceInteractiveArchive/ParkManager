package network.palace.parkmanager.shows;

import lombok.Getter;
import network.palace.core.utils.ItemUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Random;

@Getter
public class ShowEntry {
    private String showFile;
    private String region;
    private String displayName;
    private byte data;

    public ShowEntry(String showFile, String region, String displayName) {
        this.showFile = showFile;
        this.region = region;
        this.displayName = displayName;
        data = (byte) new Random().nextInt(16);
    }

    public ItemStack getItem() {
        return ItemUtil.create(Material.CONCRETE, 1, data, displayName, Arrays.asList("", ChatColor.YELLOW + "Request to start this show"));
    }
}
