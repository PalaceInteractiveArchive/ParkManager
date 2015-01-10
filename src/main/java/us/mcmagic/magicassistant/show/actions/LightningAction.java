package us.mcmagic.magicassistant.show.actions;


import us.mcmagic.magicassistant.show.Show;
import org.bukkit.Location;

public class LightningAction extends ShowAction {
    public Location Location;

    public LightningAction(Show show, long time, Location location) {
        super(show, time);

        Location = location;
    }

    @Override
    public void Play() {
        Location.getWorld().strikeLightningEffect(Location);
    }
}
