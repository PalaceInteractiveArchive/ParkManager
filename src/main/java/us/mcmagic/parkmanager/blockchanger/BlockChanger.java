package us.mcmagic.parkmanager.blockchanger;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import us.mcmagic.parkmanager.ParkManager;
import us.mcmagic.parkmanager.utils.FileUtil;

import java.io.*;
import java.util.*;

/**
 * Created by Marc on 3/8/15
 */
public class BlockChanger implements Listener {
    private List<UUID> debug = new ArrayList<>();
    private HashMap<String, Changer> changers = new HashMap<>();
    private HashMap<UUID, List<Location>> selections = new HashMap<>();
    private List<UUID> delay = new ArrayList<>();

/*
    @SuppressWarnings("deprecation")
    public BlockChanger() {
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(ParkManager.getInstance(),
                ListenerPriority.HIGHEST, PacketType.Play.Server.MAP_CHUNK) {
            @Override
            public void onPacketSending(PacketEvent event) {
                try {
                    PacketContainer container = event.getPacket();
                    StructureModifier<Integer> ints = container.getIntegers();
                    Chunk c = event.getPlayer().getWorld().getChunkAt(ints.read(0), ints.read(1));
                    ChunkSection[] sections = ((CraftChunk) c).getHandle().getSections();
                    for (ChunkSection s : sections) {
                        if (s == null) {
                            continue;
                        }
                        for (int x = 0; x < 16; x++) {
                            int y2 = 0;
                            for (int y = s.getYPosition(); y < (s.getYPosition() + 15); y++) {
                                for (int z = 0; z < 16; z++) {
                                    Block b = c.getBlock(x, y, z);
                                    IBlockData data = s.getType(x, y2, z);
                                    net.minecraft.server.v1_8_R3.Block.getById(0);
                                    int id = data.getBlock().getId(data.getBlock());
                                    Material type = Material.getMaterial(id);
                                    for (Changer changer : getChangers()) {
                                        if (isInside(b.getLocation(), changer)) {
                                            Material to = changer.getTo();
                                            s.setType(x, y2, z, net.minecraft.server.v1_8_R3.Block.getById(to.getId())
                                                    .fromLegacyData(to.getMaxDurability()));
                                        }
                                    }
                                }
                                y2++;
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        try {
            initialize();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }*/

    private boolean isInside(Location loc, Changer changer) {
        Location loc1 = changer.getFirstLocation();
        Location loc2 = changer.getSecondLocation();
        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();
        return x >= loc1.getBlockX() && x <= loc2.getBlockX() && y >= loc1.getBlockY() && y <= loc2.getBlockY() &&
                z >= loc1.getBlockX() && z <= loc2.getBlockZ();
    }

    @SuppressWarnings("deprecation")
    public void initialize() throws FileNotFoundException {
        // name;x1;y1;z1;x2;y2;z2;from;to;trigger
        Scanner scanner = new Scanner(new FileReader(FileUtil.blockchangerFile()));
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
                changers.put(name, new Changer(name, loc1, loc2, from, to, toData, sender));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /*
    private final PatcherAPI api;

    public BlockChanger() {
        api = new PatcherAPI();
        ConversionCache cache = new ConversionCache(api);
        EventScheduler scheduler = new EventScheduler();
        final ChangeCalculations calculations = new ChangeCalculations(cache, scheduler);
        ProtocolLibrary.getProtocolManager().getAsynchronousManager().registerAsyncHandler(
                new PacketAdapter(ParkManager.getInstance(), ListenerPriority.HIGHEST,
                        PacketType.Play.Server.MAP_CHUNK, PacketType.Play.Server.MAP_CHUNK_BULK) {
                    @Override
                    public void onPacketSending(PacketEvent event) {
                        try {
                            if (event.getPacketType().equals(PacketType.Play.Server.MAP_CHUNK)) {
                                calculations.translateMapChunk(event.getPacket(), event.getPlayer());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        System.out.println(event.getPacketType().name());
                    }
                }).start();
    }

    private BlockVector getChunkCoordinate(Player player) {
        Location loc = player.getLocation();
        return new BlockVector(loc.getBlockX() >> 4, loc.getBlockY() >> 4, loc.getBlockZ() >> 4);
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onChunk(ChunkPostProcessingEvent event) {
        BlockVector last = glassChunk.get(event.getPlayer().getUniqueId());
        // Convert to glass
        if (last != null && (last.getBlockX() == event.getChunkX() && last.getBlockZ() == event.getChunkZ())) {
            SegmentLookup lookup = event.getLookup();

            int glass = Material.GLASS.getId();

            // To glass
            lookup.setBlockLookup(Material.BEDROCK.getId(), glass);
            lookup.setBlockLookup(Material.BRICK.getId(), glass);
            lookup.setBlockLookup(Material.CLAY.getId(), glass);
            lookup.setBlockLookup(Material.DIRT.getId(), glass);
            lookup.setBlockLookup(Material.GRASS.getId(), glass);
            lookup.setBlockLookup(Material.GRAVEL.getId(), glass);
            lookup.setBlockLookup(Material.ICE.getId(), glass);
            lookup.setBlockLookup(Material.OBSIDIAN.getId(), glass);
            lookup.setBlockLookup(Material.NETHER_BRICK.getId(), glass);
            lookup.setBlockLookup(Material.NETHERRACK.getId(), glass);
            lookup.setBlockLookup(Material.SAND.getId(), glass);

            lookup.setBlockLookup(Material.SOUL_SAND.getId(), glass);
            lookup.setBlockLookup(Material.STONE.getId(), glass);

            // To air
            lookup.setBlockLookup(Material.SNOW.getId(), 0);
            lookup.setBlockLookup(Material.WATER.getId(), 0);

            lookup.setBlockLookup(Material.LAPIS_BLOCK.getId(), glass);
            lookup.setBlockLookup(Material.IRON_BLOCK.getId(), glass);
        }
    }
    */

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
        Bukkit.getScheduler().runTaskAsynchronously(ParkManager.getInstance(), () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                changers.values().stream().filter(changer -> changer.getFirstLocation()
                        .distance(player.getLocation()) < 75).forEach(changer -> changer.sendReverse(player));
            }
            changers.clear();
            /*
            try {
                initialize();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }*/
        });
    }

    public List<Changer> getChangers() {
        return new ArrayList<>(changers.values());
    }

    public List<String> changerList() {
        List<String> list = new ArrayList<>();
        int i = 0;
        for (Map.Entry<String, Changer> entry : changers.entrySet()) {
            list.add(i + ": " + entry.getKey());
            i++;
        }
        return list;
    }

    public boolean toggleDebug(final Player player) {
        UUID uuid = player.getUniqueId();
        if (debug.contains(uuid)) {
            debug.remove(uuid);
            return true;
        }
        debug.add(uuid);
        Bukkit.getScheduler().runTaskAsynchronously(ParkManager.getInstance(), () ->
                changers.values().stream().filter(changer -> isClose(player, changer.getFirstLocation(),
                        changer.getSecondLocation())).forEach(changer -> changer.sendReverse(player)));
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
        return changers.get(name);
    }

    public void removeChanger(String name) {
        changers.remove(name);
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
        if (changers.isEmpty()) {
            return;
        }
        for (Changer changer : changers.values()) {
            /*
              What does canSend() do?
                [] <-- Player head (I know, it's ugly)
                /\ <-- Player feet
              [][] <-- Check to see if this block
              [][] <-- or this block equal changer.getSender()
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
        changers.put(changer.getName(), changer);
        updateFile();
    }

    public void updateFile() throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(FileUtil.blockchangerFile(), false));
        for (Changer ch : changers.values()) {
            bw.write(ch.toString());
            bw.newLine();
        }
        bw.close();
    }

    public void logout(Player player) {
        delay.remove(player.getUniqueId());
    }
}