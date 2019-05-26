package network.palace.parkmanager.food;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

@Getter
@AllArgsConstructor
public class FoodLocation {
    private int id;
    private String name;
    private String warp;
    private ItemStack item;
}