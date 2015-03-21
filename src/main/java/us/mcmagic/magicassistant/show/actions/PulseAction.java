package us.mcmagic.magicassistant.show.actions;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import us.mcmagic.magicassistant.show.Show;

public class PulseAction extends ShowAction {
    public Location Location;

    public PulseAction(Show show, long time, Location location) {
        super(show, time);

        Location = location;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void play() {
        Block pre = Location.getBlock();
        Location.getBlock().setType(Material.REDSTONE_BLOCK);
        Location.getBlock().setType(pre.getType());
        Location.getBlock().setData(pre.getData());
    }
}
