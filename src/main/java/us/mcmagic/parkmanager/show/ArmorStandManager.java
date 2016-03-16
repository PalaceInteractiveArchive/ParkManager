package us.mcmagic.parkmanager.show;

import net.minecraft.server.v1_8_R3.Entity;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftArmorStand;
import org.bukkit.entity.ArmorStand;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;
import us.mcmagic.parkmanager.ParkManager;
import us.mcmagic.parkmanager.show.handlers.armorstand.Movement;
import us.mcmagic.parkmanager.show.handlers.armorstand.Position;
import us.mcmagic.parkmanager.show.handlers.armorstand.ShowStand;
import us.mcmagic.parkmanager.show.handlers.armorstand.StandAction;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marc on 10/11/15
 */
public class ArmorStandManager {
    private List<ShowStand> move = new ArrayList<>();
    private List<ShowStand> pos = new ArrayList<>();

    public ArmorStandManager() {
        start();
    }

    private void start() {
        Bukkit.getScheduler().runTaskTimer(ParkManager.getInstance(), new Runnable() {
            @Override
            public void run() {
                for (ShowStand stand : new ArrayList<>(move)) {
                    ArmorStand armor = stand.getStand();
                    if (armor == null) {
                        move.remove(stand);
                        continue;
                    }
                    Movement movement = stand.getMovement();
                    Vector motion = movement.getMotion();
                    Entity e = ((CraftArmorStand) armor).getHandle();
                    e.motX = motion.getX();
                    e.motY = motion.getY();
                    e.motZ = motion.getZ();
                    e.velocityChanged = true;
                    movement.setDuration(movement.getDuration() - 1);
                    if (movement.getDuration() < 0) {
                        move.remove(stand);
                    }
                }
                for (ShowStand stand : new ArrayList<>(pos)) {
                    ArmorStand armor = stand.getStand();
                    Position position = stand.getPosition();
                    Vector motion = position.getMotion();
                    switch (position.getPositionType()) {
                        case HEAD: {
                            EulerAngle cur = armor.getHeadPose();
                            armor.setHeadPose(new EulerAngle(cur.getX() + motion.getX(), cur.getY() + motion.getY(),
                                    cur.getZ() + motion.getZ()));
                            break;
                        }
                        case BODY: {
                            EulerAngle cur = armor.getBodyPose();
                            armor.setBodyPose(new EulerAngle(cur.getX() + motion.getX(), cur.getY() + motion.getY(),
                                    cur.getZ() + motion.getZ()));
                            break;
                        }
                        case ARM_LEFT: {
                            EulerAngle cur = armor.getLeftArmPose();
                            armor.setLeftArmPose(new EulerAngle(cur.getX() + motion.getX(), cur.getY() + motion.getY(),
                                    cur.getZ() + motion.getZ()));
                            break;
                        }
                        case ARM_RIGHT: {
                            EulerAngle cur = armor.getRightArmPose();
                            armor.setRightArmPose(new EulerAngle(cur.getX() + motion.getX(), cur.getY() + motion.getY(),
                                    cur.getZ() + motion.getZ()));
                            break;
                        }
                        case LEG_LEFT: {
                            EulerAngle cur = armor.getLeftLegPose();
                            armor.setLeftLegPose(new EulerAngle(cur.getX() + motion.getX(), cur.getY() + motion.getY(),
                                    cur.getZ() + motion.getZ()));
                            break;
                        }
                        case LEG_RIGHT: {
                            EulerAngle cur = armor.getRightLegPose();
                            armor.setRightLegPose(new EulerAngle(cur.getX() + motion.getX(), cur.getY() + motion.getY(),
                                    cur.getZ() + motion.getZ()));
                            break;
                        }
                    }
                    position.setDuration(position.getDuration() - 1);
                    if (position.getDuration() < 0) {
                        pos.remove(stand);
                    }
                }
            }
        }, 0L, 1L);
    }

    public void addStand(ShowStand stand, StandAction action) {
        switch (action) {
            case MOVE:
                move.add(stand);
                break;
            case POSITION:
                pos.add(stand);
                break;
        }
    }
}