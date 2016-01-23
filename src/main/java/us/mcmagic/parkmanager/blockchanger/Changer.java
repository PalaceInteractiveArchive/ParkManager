package us.mcmagic.parkmanager.blockchanger;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import us.mcmagic.parkmanager.ParkManager;

import java.util.*;

/**
 * Created by Marc on 3/8/15
 */
public class Changer {
    private String name;
    private Location loc1;
    private Location loc2;
    private HashMap<Material, Byte> from;
    private Material to;
    private Byte toData;
    private Material sender;

    public Changer(String name, Location loc1, Location loc2, HashMap<Material,
            Byte> from, Material to, Byte toData, Material sender) {
        this.name = name;
        this.loc1 = loc1;
        this.loc2 = loc2;
        this.from = from;
        this.to = to;
        this.toData = toData;
        this.sender = sender;
    }

    public Material getSender() {
        return sender;
    }

    public String getName() {
        return name;
    }

    public Location getFirstLocation() {
        return loc1;
    }

    public Location getSecondLocation() {
        return loc2;
    }

    public List<Material> getFrom() {
        List<Material> list = new ArrayList<>();
        Set<Material> set = from.keySet();
        for (Material m : set) {
            list.add(m);
        }
        return list;
    }

    public Material getTo() {
        return to;
    }

    @SuppressWarnings("deprecation")
    public void sendReverse(final Player player) {
        Bukkit.getScheduler().runTaskAsynchronously(ParkManager.getInstance(), new Runnable() {
            @Override
            public void run() {
                for (int x = loc1.getBlockX(); x <= loc2.getBlockX(); x++) {
                    for (int y = loc1.getBlockY(); y <= loc2.getBlockY(); y++) {
                        for (int z = loc1.getBlockZ(); z <= loc2.getBlockZ(); z++) {
                            Block b = loc1.getWorld().getBlockAt(new Location(loc1.getWorld(), x, y, z));
                            player.sendBlockChange(b.getLocation(), b.getType(), b.getData());
                        }
                    }
                }
            }
        });
    }

    @SuppressWarnings("deprecation")
    public void send(final Player player) {
        for (int x = loc1.getBlockX(); x <= loc2.getBlockX(); x++) {
            for (int y = loc1.getBlockY(); y <= loc2.getBlockY(); y++) {
                for (int z = loc1.getBlockZ(); z <= loc2.getBlockZ(); z++) {
                    Chunk c = loc1.getWorld().getBlockAt(new Location(loc1.getWorld(), x, y, z)).getChunk();
                    if (!c.isLoaded()) {
                        c.load();
                    }
                }
            }
        }
        Bukkit.getScheduler().runTaskAsynchronously(ParkManager.getInstance(), new Runnable() {
            @Override
            public void run() {
                for (int x = loc1.getBlockX(); x <= loc2.getBlockX(); x++) {
                    for (int y = loc1.getBlockY(); y <= loc2.getBlockY(); y++) {
                        for (int z = loc1.getBlockZ(); z <= loc2.getBlockZ(); z++) {
                            Block b = loc1.getWorld().getBlockAt(new Location(loc1.getWorld(), x, y, z));
                            Location loc = b.getLocation();
                            for (Map.Entry<Material, Byte> fromEntry : from.entrySet()) {
                                if (fromEntry.getValue() != null) {
                                    if (b.getData() != fromEntry.getValue()) {
                                        continue;
                                    }
                                }
                                if (b.getType().equals(fromEntry.getKey())) {
                                    player.sendBlockChange(loc, to, toData);
                                }
                            }
                        }
                    }
                }
            }
        });
    }

    @SuppressWarnings("deprecation")
    private String materialList() {
        String s = "";
        int i = 0;
        if (from.isEmpty()) {
            return "0";
        }
        for (Map.Entry<Material, Byte> entry : from.entrySet()) {
            s += entry.getKey().getId();
            if (entry.getValue() != null) {
                s += ":" + entry.getValue();
            }
            i++;
            if (i >= from.size()) {
                break;
            } else {
                s += ",";
            }
        }
        return s;
    }

    @SuppressWarnings("deprecation")
    public String toString() {
        return name + ";" + loc1.getBlockX() + ";" + loc1.getBlockY() + ";" + loc1.getBlockZ() + ";" +
                loc2.getBlockX() + ";" + loc2.getBlockY() + ";" + loc2.getBlockZ() + ";" + materialList() + ";" +
                to.getId() + ":" + toData + ";" + sender.getId();
        //name;x1;y1;z1;x2;y2;z2;1:0,35:4;35:15;bedrock
    }
}
