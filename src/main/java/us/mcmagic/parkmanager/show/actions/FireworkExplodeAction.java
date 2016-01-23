package us.mcmagic.parkmanager.show.actions;

import org.bukkit.entity.Firework;
import us.mcmagic.parkmanager.show.Show;

/**
 * Created by Marc on 7/1/15
 */
public class FireworkExplodeAction extends ShowAction {
    private final Firework fw;

    public FireworkExplodeAction(Show show, long time, Firework fw) {
        super(show, time);
        this.fw = fw;
    }

    @Override
    public void play() {
        fw.detonate();
    }
}