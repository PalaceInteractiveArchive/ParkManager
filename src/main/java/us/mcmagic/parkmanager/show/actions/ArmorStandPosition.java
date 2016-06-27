package us.mcmagic.parkmanager.show.actions;

import org.bukkit.Bukkit;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;
import us.mcmagic.parkmanager.ParkManager;
import us.mcmagic.parkmanager.show.Show;
import us.mcmagic.parkmanager.show.handlers.armorstand.Position;
import us.mcmagic.parkmanager.show.handlers.armorstand.PositionType;
import us.mcmagic.parkmanager.show.handlers.armorstand.ShowStand;
import us.mcmagic.parkmanager.show.handlers.armorstand.StandAction;

/**
 * Created by Marc on 10/24/15
 */
public class ArmorStandPosition extends ShowAction {
    private ShowStand stand;
    private PositionType positionType;
    private EulerAngle angle;
    private double speed;

    public ArmorStandPosition(Show show, long time, ShowStand stand, PositionType positionType, EulerAngle angle, double speed) {
        super(show, time);
        this.stand = stand;
        this.positionType = positionType;
        this.angle = angle;
        this.speed = speed;
    }

    @Override
    public void play() {
        if (!stand.hasSpawned()) {
            Bukkit.broadcast("ArmorStand with ID " + stand.getId() + " has not spawned", "arcade.bypass");
            return;
        }
        EulerAngle a = null;
        switch (positionType) {
            case HEAD:
                a = stand.getStand().getHeadPose();
                break;
            case BODY:
                a = stand.getStand().getBodyPose();
                break;
            case ARM_LEFT:
                a = stand.getStand().getLeftArmPose();
                break;
            case ARM_RIGHT:
                a = stand.getStand().getRightArmPose();
                break;
            case LEG_LEFT:
                a = stand.getStand().getLeftLegPose();
                break;
            case LEG_RIGHT:
                a = stand.getStand().getRightLegPose();
                break;
        }
        double x = ((float) (((float) (angle.getX() - a.getX())) / (20 * speed)));
        double y = ((float) (((float) (angle.getY() - a.getY())) / (20 * speed)));
        double z = ((float) (((float) (angle.getZ() - a.getZ())) / (20 * speed)));
        Vector motion = new Vector(x, y, z);
        stand.addPosition(new Position(motion, speed * 20, positionType));
        ParkManager.armorStandManager.addStand(stand, StandAction.POSITION);
    }
}