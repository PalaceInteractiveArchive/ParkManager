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
    private String region;
    private String displayName;
    private String command;
    private byte data;

    public ShowEntry(String command, String region, String displayName) {
        this.region = region;
        this.displayName = displayName;
        this.command = command;
        data = (byte) new Random().nextInt(16);
    }

    public ItemStack getItem() {
        return ItemUtil.create(Material.CONCRETE, 1, data, displayName, Arrays.asList("", ChatColor.YELLOW + "Request to start this show"));
    }
}
