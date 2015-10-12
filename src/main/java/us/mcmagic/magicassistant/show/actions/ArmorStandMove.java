package us.mcmagic.magicassistant.show.actions;

import org.bukkit.util.Vector;
import us.mcmagic.magicassistant.MagicAssistant;
import us.mcmagic.magicassistant.show.Show;
import us.mcmagic.magicassistant.show.handlers.armorstand.Movement;
import us.mcmagic.magicassistant.show.handlers.armorstand.ShowStand;

/**
 * Created by Marc on 10/11/15
 */
public class ArmorStandMove extends ShowAction {
    private ShowStand stand;
    private Vector motion;
    private double speed;

    public ArmorStandMove(Show show, long time, ShowStand stand, Vector motion, double speed) {
        super(show, time);
        this.stand = stand;
        this.motion = motion;
        this.speed = speed;
    }

    @Override
    public void play() {
        stand.setMotion(new Movement(motion, speed));
        MagicAssistant.armorStandManager.addStand(stand);
    }
}