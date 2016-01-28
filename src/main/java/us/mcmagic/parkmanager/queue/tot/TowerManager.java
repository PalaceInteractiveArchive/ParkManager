package us.mcmagic.parkmanager.queue.tot;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.CommandBlock;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.material.Ladder;

import java.util.Random;

/**
 * Created by Marc on 12/30/15
 */
public class TowerManager implements Listener {
    private DropTower echo = DropTower.ECHO;
    private DropTower foxtrot = DropTower.FOXTROT;
    private World world;

    public TowerManager(World world) {
        this.world = world;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!player.isInsideVehicle()) {
            return;
        }
        Entity e = player.getVehicle();
        if (!e.getType().equals(EntityType.MINECART)) {
            return;
        }
        Location from = event.getFrom();
        Location to = event.getTo();
        if (from.getBlockX() != to.getBlockX() || from.getBlockZ() != to.getBlockZ()) {
            return;
        }
        int fromY = from.getBlockY();
        int toY = to.getBlockY();
        if (fromY == toY) {
            return;
        }
        if (Math.abs(fromY - toY) > 1) {
            int smaller = toY > fromY ? fromY : toY;
            int bigger = toY < fromY ? fromY : toY;
            for (int y = smaller; y < bigger; y++) {
                blockCheck(new Location(world, from.getBlockX(), y, from.getBlockZ()));
            }
        } else {
            blockCheck(to);
        }
    }

    private void blockCheck(Location loc) {
        Block b = loc.getBlock();
        if (!b.getType().equals(Material.LADDER)) {
            return;
        }
        Ladder ladder = ((Ladder) b.getState().getData());
        Block behind = b.getRelative(ladder.getFacing().getOppositeFace());
        if (!behind.getType().equals(Material.COMMAND)) {
            return;
        }
        CommandBlock cmd = (CommandBlock) behind.getState();
        String command = cmd.getCommand();
        String[] coords = command.split(" ");
        Location setBlock;
        try {
            setBlock = new Location(world, Double.parseDouble(coords[0]), Double.parseDouble(coords[1]),
                    Double.parseDouble(coords[2]));
        } catch (Exception ignored) {
            return;
        }
        setBlock.getBlock().setType(Material.REDSTONE_BLOCK);
    }

    public void randomizeTower(DropTower tower) {
        switch (tower) {
            case ECHO:
                echo.randomizeLayout();
                break;
            case FOXTROT:
                foxtrot.randomizeLayout();
                break;
        }
    }

    public void setStations(DropTower tower) {
        TowerLayout layout = tower.getLayout();
        int x1 = -188;
        int x2 = -189;
        int y1 = -122;
        int y2 = -123;
        Block high1 = world.getBlockAt(x1, layout.getHigh(), y1);
        Block high2 = world.getBlockAt(x2, layout.getHigh(), y2);
        Block min1 = world.getBlockAt(x1, layout.getLow(), y1);
        Block min2 = world.getBlockAt(x2, layout.getLow(), y2);
        double highLength = getRandomBetween(25, 50) / 100;
        double lowLength = getRandomBetween(25, 50) / 100;
    }

    private int getRandomBetween(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min + 1) + min;
    }
}