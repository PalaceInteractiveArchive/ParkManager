package us.mcmagic.magicassistant.show;

import net.minecraft.server.v1_8_R3.Entity;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftArmorStand;
import org.bukkit.entity.ArmorStand;
import org.bukkit.util.Vector;
import us.mcmagic.magicassistant.MagicAssistant;
import us.mcmagic.magicassistant.show.handlers.armorstand.Movement;
import us.mcmagic.magicassistant.show.handlers.armorstand.ShowStand;
import us.mcmagic.magicassistant.show.handlers.armorstand.StandAction;

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
        Bukkit.getScheduler().runTaskTimer(MagicAssistant.getInstance(), new Runnable() {
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