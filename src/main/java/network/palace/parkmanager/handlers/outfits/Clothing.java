package network.palace.parkmanager.handlers.outfits;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Clothing {
    private ItemStack head = null;
    private int headID = 0;
    private ItemStack shirt = null;
    private int shirtID = 0;
    private ItemStack pants = null;
    private int pantsID = 0;
    private ItemStack boots = null;
    private int bootsID = 0;
}