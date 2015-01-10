package us.mcmagic.magicassistant.show.actions;

import us.mcmagic.magicassistant.show.Show;
import org.bukkit.Location;
import org.bukkit.Material;

public class PulseAction extends ShowAction {
    public Location Location;

    public PulseAction(Show show, long time, Location location) {
        super(show, time);

        Location = location;
    }

    @Override
    public void Play() {
        Material pre = Location.getBlock().getType();
        Location.getBlock().setType(Material.REDSTONE_BLOCK);
        Location.getBlock().setType(pre);
    }
}
