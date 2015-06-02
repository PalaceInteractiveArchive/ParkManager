package us.mcmagic.magicassistant.blockchanger;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import us.mcmagic.magicassistant.MagicAssistant;

import java.io.*;
import java.util.*;

/**
 * Created by Marc on 3/8/15
 */
public class BlockChanger implements Listener {
    private List<UUID> debug = new ArrayList<>();
    private HashMap<String, Changer> blocks = new HashMap<>();
    private HashMap<UUID, List<Location>> selections = new HashMap<>();
    private List<UUID> delay = new ArrayList<>();

    @SuppressWarnings("deprecation")
    public void initialize() throws FileNotFoundException {
        Scanner scanner = new Scanner(new FileReader("plugins/MagicAssistant/blockchanger.yml"));
        while (scanner.hasNextLine()) {
            String[] args = scanner.nextLine().split(";");
            try {
                String name = args[0];
                World world = Bukkit.getWorlds().get(0);
                Location loc1 = new Location(world, Integer.parseInt(args[1]), Integer.parseInt(args[2]),
                        Integer.parseInt(args[3]));
                Location loc2 = new Location(world, Integer.parseInt(args[4]), Integer.parseInt(args[5]),
                        Integer.parseInt(args[6]));
                String[] mats = args[7].split(",");
                HashMap<Material, Byte> from = blocksFromString(args[7]);
                for (String s : mats) {
                    String[] list = s.split(":");
                    if (list.length == 1) {
                        from.put(Material.getMaterial(Integer.parseInt(list[0])), null);
                    } else {
                        from.put(Material.getMaterial(Integer.parseInt(list[0])), Byte.valueOf(list[1]));
                    }
                }
                String[] l2 = args[8].split(":");
                Material to;
                Byte toData;
                if (l2.length == 1) {
                    to = Material.getMaterial(Integer.parseInt(l2[0]));
                    toData = (byte) 0;
                } else {
                    to = Material.getMaterial(Integer.parseInt(l2[0]));
                    toData = Byte.valueOf(l2[1]);
                }
                Material sender = Material.getMaterial(Integer.parseInt(args[9]));
                blocks.put(name, new Changer(name, loc1, loc2, from, to, toData, sender));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        /**
         * Example:  name;x1;y1;z1;x2;y2;z2;stone,grass,dirt,etc.;35:15;bedrock
         * What does this do? Changes all stone,grass,dirt between loc1 and loc2
         * to black wool, only when a player is standing over a piece of bedrock.
         */
        Bukkit.getLogger().info("Loaded " + blocks.size() + " Changers!");
    }

    public void setSelection(int number, Player player, Location loc) {
        player.sendMessage(ChatColor.LIGHT_PURPLE + "Set Changer selection point " + (number + 1) + " to " + loc.getBlockX() +
                ", " + loc.getBlockY() + ", " + loc.getBlockZ());
        UUID uuid = player.getUniqueId();
        if (selections.containsKey(uuid)) {
            List<Location> list = selections.get(uuid);
            selections.remove(uuid);
            list.set(number, loc);
            selections.put(uuid, list);
            return;
        }
        if (number == 0) {
            selections.put(uuid, Arrays.asList(loc, null));
            return;
        }
        selections.put(uuid, Arrays.asList(null, loc));
    }

    public Location getSelection(int number, Player player) {
        Location loc = null;
        for (Map.Entry<UUID, List<Location>> entry : selections.entrySet()) {
            if (!entry.getKey().equals(player.getUniqueId())) {
                continue;
            }
            loc = entry.getValue().get(number);
            break;
        }
        return loc;
    }

    public void clearSelection(UUID uuid) {
        selections.remove(uuid);
    }

    @SuppressWarnings("deprecation")
    public HashMap<Material, Byte> blocksFromString(String string) {
        HashMap<Material, Byte> map = new HashMap<>();
        String[] list = string.split(",");
        if (list.length == 1) {
            String[] l = list[0].split(":");
            if (l.length == 1) {
                map.put(Material.getMaterial(Integer.parseInt(list[0])), null);
            } else {
                map.put(Material.getMaterial(Integer.parseInt(l[0])), Byte.valueOf(l[1]));
            }
        } else {
            for (String s : list) {
                String[] l = s.split(":");
                if (l.length == 1) {
                    map.put(Material.getMaterial(Integer.parseInt(s)), null);
                } else {
                    map.put(Material.getMaterial(Integer.parseInt(l[0])), Byte.valueOf(l[1]));
                }
            }
        }
        return map;
    }

    public void reload() throws FileNotFoundException {
        for (UUID uuid : debug) {
            Bukkit.getPlayer(uuid).sendMessage(ChatColor.RED + "You were removed from Changer Debug by a reload!");
        }
        debug.clear();
        delay.clear();
        Bukkit.getScheduler().runTaskAsynchronously(MagicAssistant.getInstance(), new Runnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    for (Changer changer : blocks.values()) {
                        if (changer.getFirstLocation().distance(player.getLocation()) < 75) {
                            changer.sendReverse(player);
                        }
                    }
                }
                blocks.clear();
                try {
                    initialize();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public List<Changer> getChangers() {
        return new ArrayList<>(blocks.values());
    }

    public List<String> changerList() {
        List<String> list = new ArrayList<>();
        int i = 0;
        for (Map.Entry<String, Changer> entry : blocks.entrySet()) {
            list.add(i + ": " + entry.getKey());
            i++;
        }
        return list;
    }

    public boolean toggleDebug(Player player) {
        UUID uuid = player.getUniqueId();
        if (debug.contains(uuid)) {
            debug.remove(uuid);
            return true;
        }
        debug.add(uuid);
        Bukkit.getScheduler().runTaskAsynchronously(MagicAssistant.getInstance(), new Runnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    for (Changer changer : blocks.values()) {
                        if (isClose(player, changer.getFirstLocation(), changer.getSecondLocation())) {
                            changer.sendReverse(player);
                        }
                    }
                }
            }
        });
        return false;
    }

    private boolean isClose(Player player, Location... locs) {
        Location loc = player.getLocation();
        for (Location loc2 : locs) {
            if (Math.abs(loc.getBlockX() - loc2.getBlockX()) < 75 || Math.abs(loc.getBlockZ() - loc2.getBlockZ()) < 75) {
                return true;
            }
        }
        return false;
    }

    public Changer getChanger(String name) {
        return blocks.get(name);
    }

    public void removeChanger(String name) {
        blocks.remove(name);
        try {
            updateFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        final Player player = event.getPlayer();
        Location from = event.getFrom();
        final Location to = event.getTo();
        if ((from.getBlockX() == to.getBlockX()) && (from.getBlockZ() == to.getBlockZ())) {
            return;
        }
        if (debug.contains(player.getUniqueId())) {
            return;
        }
        if (blocks.isEmpty()) {
            return;
        }
        for (Changer changer : blocks.values()) {
            /**
             * What does canSend() do?
             *   [] <-- Player head (I know, it's ugly)
             *   /\ <-- Player feet
             * [][] <-- Check to see if this block
             * [][] <-- or this block equal changer.getSender()
             */
            if (!canSend(changer.getSender(), to.getBlock()) || !isClose(player, changer.getFirstLocation(),
                    changer.getSecondLocation())) {
                continue;
            }
            changer.send(player);
        }
    }

    private boolean canSend(Material sender, Block b) {
        return b.getRelative(0, -1, 0).getType().equals(sender) || b.getRelative(0, -2, 0).getType().equals(sender);
    }

    public void addChanger(Changer changer) throws IOException {
        blocks.put(changer.getName(), changer);
        updateFile();
    }

    public void updateFile() throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter("plugins/MagicAssistant/blockchanger.yml", false));
        for (Changer ch : blocks.values()) {
            bw.write(ch.toString());
            bw.newLine();
        }
        bw.close();
    }

    public void logout(Player player) {
        delay.remove(player.getUniqueId());
    }
}
