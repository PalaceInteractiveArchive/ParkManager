package network.palace.parkmanager.mural;

import lombok.Getter;
import network.palace.core.message.FormattedMessage;
import network.palace.core.player.CPlayer;
import network.palace.parkmanager.utils.MuralUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapPalette;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MuralSection extends MapRenderer {
    // First array is rows, second is columns. So [0][7] is top right, [7][0] is bottom left
    @Getter private MuralSquare[][] squares;
    @Getter private Mural mural;
    @Getter private int id;
    @Getter private int mapId;
    @Getter private BufferedImage image;
    @Getter public int xCap;
    @Getter public int yCap;
    @Getter private byte[] data;
    private List<UUID> rendered = new ArrayList<>();
    private static FormattedMessage msg = new FormattedMessage("Thanks for helping out paint our mural! When it's all " +
            "finished, we'll post the complete picture to our ").color(ChatColor.YELLOW).then("Twitter")
            .color(ChatColor.AQUA).link("https://palnet.us/muraltwitter").tooltip(ChatColor.AQUA + "Click to visit our Twitter!")
            .then(". Share a picture of what you painted with ").color(ChatColor.YELLOW).then("#PalaceMural!")
            .color(ChatColor.AQUA).link("https://palnet.us/muraltweets").tooltip(ChatColor.AQUA + "Click to see tweets with #PalaceMural!");

    public MuralSection(Mural mural, int id, int mapId) {
        this(mural, id, mapId, null);
    }

    public MuralSection(Mural mural, int id, int mapId, BufferedImage image) {
        this.mural = mural;
        this.id = id;
        this.mapId = mapId;
        if (image == null) {
            image = mural.getImageFromId(id);
        }
        this.image = image;
        this.xCap = image.getWidth();
        this.yCap = image.getHeight();
        this.data = MapPalette.imageToBytes(image);
        this.squares = new MuralSquare[8][8];
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                this.squares[x][y] = new MuralSquare(x, y);
            }
        }
        File file = new File("plugins/Painting/images/" + mural.getName() + "/squares.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        YamlConfiguration squares = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection main = squares.getConfigurationSection("squares");
        if (main == null) {
            main = squares.createSection("squares");
        }
        ConfigurationSection localSection = main.getConfigurationSection("id_" + getId());
        if (localSection == null) {
            localSection = main.createSection("id_" + getId());
        }
        List<String> players = localSection.getStringList("players");
        // '0,1;uuid;time'
        for (String p : players) {
            try {
                String[] l = p.split(";");
                String loc = l[0];
                UUID uuid = UUID.fromString(l[1]);
                long time = MuralUtil.parseLong(l[2]);
                String[] l2 = loc.split(",");
                int x = MuralUtil.parseInt(l2[0]);
                int y = MuralUtil.parseInt(l2[1]);
                this.squares[x][y] = new MuralSquare(uuid, time, x, y);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            squares.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void render(MapView map, MapCanvas canvas, Player player) {
        if (rendered.contains(player.getUniqueId())) {
            // Already sent this player the map, no need to send it again
            return;
        }
        MuralUtil.emptyGrid(canvas);
//        for (int x = 0; x < 128; x++) {
//            for (int y = 0; y < 128; y++) {
//                canvas.setPixel(x, y, data[y * this.yCap + x]);
//            }
//        }
        for (MuralSquare[] row : squares) {
            for (MuralSquare square : row) {
                if (!square.isPainted()) continue;
                int localX = square.getX();
                int localY = square.getY();
                int realX = localX * 16;
                int realY = localY * 16;
                for (int x = realX; x < realX + 16; x++) {
                    for (int y = realY; y < realY + 16; y++) {
                        canvas.setPixel(x, y, data[(y * this.yCap + x)]);
                    }
                }
            }
        }
        rendered.add(player.getUniqueId());
    }

    public void paint(CPlayer player, MuralSquare square, ItemFrame frame) {
        rendered.clear();
        square.paint(player);
        msg.send(player);
        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_NOTE_SNARE, 1, 0);
        player.giveHonor(1);
        player.giveAchievement(23);
        if (frame != null) {
            int relX = square.getX();
            int relY = square.getY();
            Location loc = frame.getLocation().add(-0.5, 0.5, -0.5);
            switch (mural.getFacing()) {
                case NORTH:
                    loc.add(relX * -0.125 + 0.0625, -(relY * 0.125 + 0.0625), -0.0625);
                    break;
                case EAST:
                    loc.add(-0.0625, -(relY * 0.125 + 0.0625), relX * -0.125 + 0.0625);
                    break;
                case SOUTH:
                    loc.add(relX * 0.125 + 0.0625, -(relY * 0.125 + 0.0625), 1.0625);
                    break;
                case WEST:
                    loc.add(1.0625, -(relY * 0.125 + 0.0625), relX * 0.125 + 0.0625);
                    break;
            }
            player.getWorld().spawnParticle(Particle.SPELL_WITCH, loc, 1, 0, 0, 0);
        }
        mural.saveToFile();
    }

}
