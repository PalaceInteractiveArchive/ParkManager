package us.mcmagic.parkmanager.pixelator.renderer;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import us.mcmagic.parkmanager.pixelator.util.FilesystemCache;

import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings({"unchecked", "deprecation"})
public abstract class ImageRenderer extends MapRenderer {

    protected short id;
    protected BufferedImage image;
    public int oX = 0;
    public int oY = 0;
    public int xCap = 0;
    public int yCap = 0;
    protected Set rendered;

    public ImageRenderer(short id, BufferedImage image) {
        this.id = id;
        this.image = image;
        this.rendered = new HashSet();
        this.activate();
    }

    public byte[] getCache(Player p) {
        return FilesystemCache.getByteData(this);
    }

    public void render(MapView view, MapCanvas canvas, Player p) {
        String name = p.getName();
        if (!this.rendered.contains(name)) {
            byte[] data = getCache(p);
            for (int x2 = 0; x2 < xCap; ++x2) {
                for (int y2 = 0; y2 < yCap; ++y2) {
                    canvas.setPixel(this.oX + x2, this.oY + y2, data[y2 * yCap + x2]);
                }
            }
            this.rendered.add(name);
            p.sendMap(view);
        }

    }

    protected static short generateMapId() {
        return Bukkit.createMap(Bukkit.getWorlds().get(0)).getId();
    }

    public void deactivate() {
        MapView m = Bukkit.getMap(this.id);
        for (MapRenderer mr : m.getRenderers()) {
            m.removeRenderer(mr);
        }

    }

    public void activate() {
        this.deactivate();
        Bukkit.getMap(this.id).addRenderer(this);
    }

    public ItemStack assign(ItemStack i) {
        if (i.getType() != Material.MAP) {
            throw new IllegalArgumentException("This item is not a map");
        } else {
            i.setDurability(this.id);
            return i;
        }
    }

    public ItemStack createMap() {
        return new ItemStack(Material.MAP, 1, this.id);
    }

    public void handleQuit(Player p) {
        this.rendered.remove(p.getName());
    }

    public short getId() {
        return this.id;
    }

    public BufferedImage getImage() {
        return this.image;
    }

    public Set getRendered() {
        return Collections.unmodifiableSet(this.rendered);
    }

    public String toString() {
        return Short.toString(this.id);
    }
}
