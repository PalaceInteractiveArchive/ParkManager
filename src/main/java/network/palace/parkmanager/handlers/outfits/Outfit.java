package network.palace.parkmanager.handlers.outfits;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

@Getter
@AllArgsConstructor
public class Outfit {
    private int id;
    private String name;
    private ItemStack head;
    private ItemStack shirt;
    private ItemStack pants;
    private ItemStack boots;
}
