package network.palace.parkmanager.handlers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Marc on 11/9/15
 */
@Getter
@AllArgsConstructor
public class Outfit {
    private Integer id;
    private String name;
    private ItemStack head;
    private ItemStack shirt;
    private ItemStack pants;
    private ItemStack boots;
}