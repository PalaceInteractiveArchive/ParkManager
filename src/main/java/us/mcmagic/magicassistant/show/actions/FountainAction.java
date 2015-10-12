package us.mcmagic.magicassistant.show.actions;

import org.bukkit.Location;
import org.bukkit.util.Vector;
import us.mcmagic.magicassistant.MagicAssistant;
import us.mcmagic.magicassistant.show.handlers.Fountain;
import us.mcmagic.magicassistant.show.Show;

public class FountainAction extends ShowAction {
    private double duration;
    private Location loc;
    private int type;
    private byte data;
    private Vector force;

    public FountainAction(Show show, long time, Location loc, double duration, int type, byte data, Vector force) {
        super(show, time);
        this.loc = loc;
        this.duration = duration;
        this.type = type;
        this.data = data;
        this.force = force;
    }

    @Override
    public void play() {
        MagicAssistant.fountainManager.addFountain(new Fountain(loc, duration, type, data, force));
    }
}