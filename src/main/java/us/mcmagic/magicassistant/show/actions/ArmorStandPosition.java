package us.mcmagic.magicassistant.show.actions;

import org.bukkit.Location;
import us.mcmagic.magicassistant.show.Show;
import us.mcmagic.magicassistant.show.handlers.armorstand.ShowStand;

/**
 * Created by Marc on 10/24/15
 */
public class ArmorStandPosition extends ShowAction {
    private ShowStand stand;
    private final Location loc;
    private double speed;

    public ArmorStandPosition(Show show, long time, ShowStand stand, Location loc, double speed) {
        super(show, time);
        this.stand = stand;
        this.loc = loc;
        this.speed = speed;
    }

    @Override
    public void play() {
    }
}