package network.palace.parkmanager.food;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

@Getter
@AllArgsConstructor
public class FoodLocation {
    private String id;
    private String name;
    private String warp;
    private ItemStack item;
}