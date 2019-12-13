package network.palace.parkmanager.handlers;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.World;

@Getter
@AllArgsConstructor
public class Park {
    private String id;
    private World world;
    private ProtectedRegion region;
}
