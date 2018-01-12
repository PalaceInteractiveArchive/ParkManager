package network.palace.parkmanager.utils;

import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import network.palace.core.utils.ItemUtil;
import network.palace.parkmanager.mural.Mural;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapPalette;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MuralUtil {
    private List<UUID> sessions = new ArrayList<>();
    private List<Mural> murals = new ArrayList<>();
    private static ItemStack item = ItemUtil.create(Material.SPECTRAL_ARROW, ChatColor.LIGHT_PURPLE + "Paintbrush");

    public MuralUtil() {
        Core.runTask(this::reload);
    }

    public void reload() {
        murals.forEach(Mural::saveToFile);
        murals.clear();
        File dir = new File("plugins/Painting");
        if (!dir.exists()) {
            dir.mkdir();
        }
        File imdir = new File("plugins/Painting/images");
        if (!imdir.exists()) {
            imdir.mkdir();
        }
        File file = new File("plugins/Painting/murals.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection main = config.getConfigurationSection("murals");
        if (main == null) {
            main = config.createSection("murals");
        }
        for (String s : main.getKeys(false)) {
            ConfigurationSection sec = main.getConfigurationSection(s);
            List<Integer> ids = sec.getIntegerList("ids");
            int[] array = new int[ids.size()];
            for (int i = 0; i < ids.size(); i++) {
                array[i] = ids.get(i);
            }
            Mural m = new Mural(s, sec.getInt("min.x"), sec.getInt("min.y"), sec.getInt("min.z"),
                    sec.getInt("max.x"), sec.getInt("max.y"), sec.getInt("max.z"), array);
            murals.add(m);
        }
        try {
            saveToFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void join(CPlayer player) {
        if (sessions.contains(player.getUniqueId())) return;
        sessions.add(player.getUniqueId());
        player.sendMessage(ChatColor.DARK_AQUA + "                             Epcot International\n                          " +
                ChatColor.WHITE + "" + ChatColor.BOLD + "Festival of the Arts\n" + ChatColor.RESET +
                "                                " + ChatColor.LIGHT_PURPLE + "Mural Painting\n\n" + ChatColor.YELLOW +
                "Claim a paintbrush and help paint our murals! You can fill in one square every 12 hours.\n" +
                "After you've painted your square, return the paintbrush at the booth next to the mural.\n" +
                "Come back throughout the festival to help us finish different murals!");
        PlayerInventory inv = player.getInventory();
        inv.setItem(4, item);
        inv.setHeldItemSlot(4);
    }

    public void done(CPlayer player) {
        if (!sessions.remove(player.getUniqueId())) return;
        player.sendMessage(ChatColor.YELLOW + "Thanks for helping paint!");
        player.getInventory().setItem(4, InventoryUtil.getRideItem());
    }

    public static BufferedImage scaleImage(BufferedImage b, int width, int height) {
        if (b == null) return null;
        if ((b.getWidth() == width) && (b.getHeight() == height)) {
            return b;
        }
        AffineTransform a = AffineTransform.getScaleInstance((double) width / b.getWidth(), (double) height / b.getHeight());
        AffineTransformOp o = new AffineTransformOp(a, 2);
        return o.filter(b, new BufferedImage(width, height, b.getType()));
    }

    public Mural createMural(String name, int minX, int maxX, int minY, int maxY, int minZ, int maxZ) {
        Mural mural = new Mural(name, minX, maxX, minY, maxY, minZ, maxZ, new int[0]);
        murals.add(mural);
        try {
            saveToFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mural;
    }

    private void saveToFile() throws IOException {
        File file = new File("plugins/Painting/murals.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        for (Mural m : murals) {
            ConfigurationSection sec = config.getConfigurationSection("murals." + m.getName());
            if (sec == null) {
                sec = config.createSection("murals." + m.getName());
            }
            sec.set("min.x", m.getMinX());
            sec.set("min.y", m.getMinY());
            sec.set("min.z", m.getMinZ());
            sec.set("max.x", m.getMaxX());
            sec.set("max.y", m.getMaxY());
            sec.set("max.z", m.getMaxZ());
            sec.set("ids", m.getIntList());
        }
        config.save(file);
    }

    public static void clearRenderers(MapView map) {
        for (MapRenderer renderer : map.getRenderers()) {
            map.removeRenderer(renderer);
        }
    }

    public static float faceToYaw(BlockFace face) {
        switch (face) {
            case NORTH:
                return 0;
            case EAST:
                return 90;
            case SOUTH:
                return 180;
            case WEST:
                return 270;
            default:
                return 0;
        }
    }

    public List<Mural> getMurals() {
        return new ArrayList<>(murals);
    }

    public static void emptyGrid(MapCanvas canvas) {
        for (int x = 0; x < 128; x++) {
            for (int y = 0; y < 128; y++) {
                if (y % 16 == 0 || y % 16 == 15 || x % 16 == 0 || x % 16 == 15) {
                    canvas.setPixel(x, y, MapPalette.matchColor(Color.BLACK));
                } else {
                    canvas.setPixel(x, y, MapPalette.matchColor(Color.WHITE));
                }
            }
        }
    }

    public static long getLastPaint(UUID uuid, Mural mural) {
        File file = new File("plugins/Painting/images/" + mural.getName() + "/squares.yml");
        if (!file.exists()) return 0;
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection main = config.getConfigurationSection("squares");
        if (main == null) return 0;
        long lastTime = 0;
        for (String s : main.getKeys(false)) {
            ConfigurationSection sec = main.getConfigurationSection(s);
            if (sec == null) continue;
            List<String> list = sec.getStringList("players");
            for (String p : list) {
                String[] l = p.split(";");
                UUID uuid2 = UUID.fromString(l[1]);
                if (!uuid2.equals(uuid)) continue;
                long time = parseLong(l[2]);
                if (time > lastTime) {
                    lastTime = time;
                }
            }
        }
        return lastTime;
    }

    public static int parseInt(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public static long parseLong(String s) {
        try {
            return Long.parseLong(s);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
