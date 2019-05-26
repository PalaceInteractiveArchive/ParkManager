package network.palace.parkmanager.attractions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import network.palace.parkmanager.handlers.AttractionCategory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@Getter
@AllArgsConstructor
public class Attraction {
    private int id;
    @Setter private String name;
    @Setter private String warp;
    @Setter private String description;
    @Setter private List<AttractionCategory> categories;
    @Setter private boolean open;
    @Setter private ItemStack item;
}