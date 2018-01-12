package network.palace.parkmanager.mural;

import lombok.Getter;
import network.palace.core.player.CPlayer;
import network.palace.parkmanager.utils.MuralUtil;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ItemFrame;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapView;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class Mural {
    public static final BlockFace[] faces = new BlockFace[]{BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};
    private HashMap<Integer, MuralSection> sections = new HashMap<>();
    @Getter private final BlockFace facing;
    @Getter private final String name;
    @Getter private final int minX;
    @Getter private final int maxX;
    @Getter private final int minY;
    @Getter private final int maxY;
    @Getter private final int minZ;
    @Getter private final int maxZ;

    public Mural(String name, int minX, int maxX, int minY, int maxY, int minZ, int maxZ, int[] sectionIds) {
        this.name = name;
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
        this.minZ = minZ;
        this.maxZ = maxZ;

        World w = Bukkit.getWorlds().get(0);

        BlockFace back = null;
        Location temp = new Location(w, minX, minY, minZ);
        for (BlockFace face : faces) {
            Block b = temp.getBlock().getRelative(face);
            if (!b.getType().isSolid()) continue;
            back = face;
        }
        if (back != null) {
            facing = back.getOppositeFace();
        } else {
            facing = BlockFace.SOUTH;
        }
        if (sectionIds.length == 0) {
            sectionIds = new int[(maxX - minX + 1) * (maxY - minY + 1) * (maxZ - minZ + 1)];
            List<File> arr = getImageFiles();
            float yaw = MuralUtil.faceToYaw(back);

            int muralId = 0;
            switch (back) {
                case NORTH: {
                    for (int y = this.maxY; y >= this.minY; y--) {
                        for (int x = this.minX; x <= this.maxX; x++) {
                            sectionIds[muralId] = generate(w, x, y, minZ, muralId++, yaw, arr);
                        }
                    }
                    break;
                }
                case EAST: {
                    for (int y = this.maxY; y >= this.minY; y--) {
                        for (int z = this.minZ; z <= this.maxZ; z++) {
                            sectionIds[muralId] = generate(w, minX, y, z, muralId++, yaw, arr);
                        }
                    }
                    break;
                }
                case SOUTH: {
                    for (int y = this.maxY; y >= this.minY; y--) {
                        for (int x = this.maxX; x >= this.minX; x--) {
                            sectionIds[muralId] = generate(w, x, y, minZ, muralId++, yaw, arr);
                        }
                    }
                    break;
                }
                case WEST: {
                    for (int y = this.maxY; y >= this.minY; y--) {
                        for (int z = this.maxZ; z >= this.minZ; z--) {
                            sectionIds[muralId] = generate(w, minX, y, z, muralId++, yaw, arr);
                        }
                    }
                    break;
                }
            }
        }
        int[] finalSectionIds = sectionIds;
        for (int id = 0; id < finalSectionIds.length; id++) {
            int mapId = finalSectionIds[id];
            MuralSection sec = new MuralSection(Mural.this, id, mapId);
            MapView view = Bukkit.getMap((short) mapId);
            if (view != null) {
                MuralUtil.clearRenderers(view);
                view.addRenderer(sec);
            }
            sections.put(id, sec);
        }
    }

    private int generate(World w, int x, int y, int z, int muralId, float yaw, List<File> arr) {
        Location loc = new Location(w, x, y, z);

        loc.setYaw(yaw);

        ItemFrame frame = w.spawn(loc, ItemFrame.class);

        MapView map = Bukkit.createMap(w);
        int mapId = map.getId();

        MuralUtil.clearRenderers(map);

        BufferedImage image;
        try {
            image = MuralUtil.scaleImage(ImageIO.read(arr.get(muralId)), 128, 128);
        } catch (IOException e) {
            return -1;
        }

        MuralSection section = new MuralSection(this, muralId, mapId, image);

        map.addRenderer(section);

        frame.setItem(new ItemStack(Material.MAP, 1, (short) mapId));

        return mapId;
    }

    private List<File> getImageFiles() {
        File dir = new File("plugins/Painting/images/" + name);
        if (!dir.isDirectory()) {
            return new ArrayList<>();
        }
        List<File> arr = new ArrayList<>(Arrays.asList(dir.listFiles((dir1, name) -> name.endsWith(".png"))));
        arr.sort(Comparator.comparing(File::getName));
        return arr;
    }

    public boolean isInMural(int mapId) {
        return getSectionFromMapId(mapId) != null;
    }

    public MuralSection getSectionFromMapId(int id) {
        for (Map.Entry<Integer, MuralSection> section : sections.entrySet()) {
            if (section.getValue().getMapId() == id) {
                return section.getValue();
            }
        }
        return null;
    }

    public void paint(CPlayer player, int id, ItemFrame frame) {
        MuralSection section = getSectionFromMapId(id);
        if (section == null) {
            player.sendMessage(ChatColor.RED + "There was a problem painting this mural, sorry!");
            return;
        }
        MuralSquare[][] squares = section.getSquares();
        List<MuralSquare> openSquares = new ArrayList<>();
        for (MuralSquare[] rowArray : squares) {
            for (MuralSquare square : rowArray) {
                if (square.isPainted()) continue;
                openSquares.add(square);
            }
        }
        if (openSquares.isEmpty()) {
            player.sendMessage(ChatColor.RED + "This section of the mural is full, try painting another one!");
            return;
        }
        MuralSquare square = openSquares.get(new Random().nextInt(openSquares.size()));
        section.paint(player, square, frame);
    }

    public BufferedImage getImageFromId(int id) {
        List<File> arr = getImageFiles();
        try {
            return MuralUtil.scaleImage(ImageIO.read(arr.get(id)), 128, 128);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Integer> getIntList() {
        List<Integer> list = new ArrayList<>();
        for (MuralSection sec : sections.values()) {
            list.add(sec.getMapId());
        }
        return list;
    }

    public void saveToFile() {
        File file = new File("plugins/Painting/images/" + getName() + "/squares.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection main = config.getConfigurationSection("squares");
        if (main == null) {
            main = config.createSection("squares");
        }
        int i = 0;
        for (MuralSection section : sections.values()) {
            ConfigurationSection sec = main.getConfigurationSection("id_" + i++);
            if (sec == null) {
                sec = main.createSection("id_" + i);
            }
            List<String> players = new ArrayList<>();
            for (MuralSquare[] row : section.getSquares()) {
                for (MuralSquare square : row) {
                    if (!square.isPainted()) continue;
                    players.add(square.getX() + "," + square.getY() + ";" + square.getPaintedBy() + ";" + square.getPaintedAt());
                }
            }
            sec.set("players", players);
        }
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
