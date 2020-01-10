package network.palace.parkmanager.food;

import lombok.AllArgsConstructor;
import lombok.Getter;
import network.palace.parkmanager.handlers.ParkType;
import org.bukkit.inventory.ItemStack;

@Getter
@AllArgsConstructor
public class FoodLocation {
    private int id;
    private ParkType park;
    private String name;
    private String warp;
    private ItemStack item;
}