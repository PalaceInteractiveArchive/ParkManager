package us.mcmagic.magicassistant.show.actions;


import org.bukkit.Location;
import us.mcmagic.magicassistant.show.Show;

public class LightningAction extends ShowAction {
    public Location loc;

    public LightningAction(Show show, long time, Location loc) {
        super(show, time);
        this.loc = loc;
    }

    @Override
    public void play() {
        loc.getWorld().strikeLightningEffect(loc);
    }
}
