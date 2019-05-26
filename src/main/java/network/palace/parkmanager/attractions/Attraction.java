package network.palace.parkmanager.attractions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

@Getter
@AllArgsConstructor
public class Attraction {
    private int id;
    private String name;
    private String warp;
    private ItemStack item;
}