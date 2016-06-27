package us.mcmagic.parkmanager.pixelator.renderer;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import us.mcmagic.parkmanager.ParkManager;
import us.mcmagic.parkmanager.pixelator.manager.Manager;
import us.mcmagic.parkmanager.pixelator.reader.CompressedStringReader;
import us.mcmagic.parkmanager.pixelator.renderer.types.MapImageRenderer;
import us.mcmagic.parkmanager.pixelator.renderer.util.RendererList;
import us.mcmagic.parkmanager.utils.SqlUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

@SuppressWarnings("unchecked")
public class RendererManager extends Manager {

    private static final CompressedStringReader reader = new CompressedStringReader("renderers.cache", "plugins/ParkManager/");
    private RendererList renderers;


    public RendererManager(ParkManager plugin) {
        super(plugin);
        this.initialize();
    }

    public boolean initialize() {
        this.loadRenderers();
        this.registerListener();
        return true;
    }

    public void disable() {
        this.unregisterListener();
    }

    private void loadRenderers() {
        this.renderers = new RendererList();
        World w = Bukkit.getWorlds().get(0);
        w.getEntitiesByClasses(org.bukkit.entity.ItemFrame.class);
        long now = System.currentTimeMillis();
        try (Connection connection = SqlUtil.getConnection()) {
            PreparedStatement sql = connection.prepareStatement("SELECT * FROM pixelator");
            ResultSet result = sql.executeQuery();
            while (result.next()) {
                int id = result.getInt("id");
                String source = result.getString("source");
                try {
                    MapImageRenderer renderer = MapImageRenderer.load((short) id, source); //MapImageRenderer.fromString(plugin, p);
                    this.plugin.getLogger().log(Level.INFO, "Loaded renderer for " + id);
                    this.renderers.add(renderer);
                } catch (Exception var8) {
                    this.plugin.getLogger().log(Level.WARNING, "Ignoring renderer with id \'" + id + "\'. Cause: " +
                            var8.getMessage(), var8);
                }
            }
            result.close();
            sql.close();
        } catch (Exception var9) {
            this.plugin.getLogger().warning("Error getting Pixelators from Database!");
        }
        int var10 = this.renderers.size();
        this.plugin.getLogger().info(var10 + " renderer" + (var10 == 1 ? "" : "s") + " loaded in " + (System.currentTimeMillis() - now) + "ms");
    }

    private void saveRenderers() {
        if (this.renderers.size() > 0 && !reader.saveToFile(this.renderers.toString())) {
            this.plugin.getLogger().warning("Failed to save " + reader.getOuputFileName() + "!");
        }

    }

    public void register(MapImageRenderer i) {
        this.renderers.add(i);
        this.saveRenderers();
    }

    public void unregister(ImageRenderer i) {
        i.deactivate();
        this.renderers.remove(i.getId());
        this.saveRenderers();
    }

    public List getRenderers() {
        return Collections.unmodifiableList(this.renderers);
    }

    public int getRendererAmount() {
        return this.renderers.size();
    }

    public MapImageRenderer getRenderer(short id) {
        return (MapImageRenderer) this.renderers.get(id);
    }

    @EventHandler(
            priority = EventPriority.MONITOR
    )
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Entity e = event.getRightClicked();
        Player p = event.getPlayer();
        ItemStack h = p.getItemInHand();
        if (e instanceof ItemFrame && h.getType() == Material.MAP) {
            ItemFrame i = (ItemFrame) e;
            ItemStack s = i.getItem();
            if (s == null || s.getType() == Material.AIR) {
                MapImageRenderer m = (MapImageRenderer) this.renderers.get(h.getDurability());
                if (m != null) {
                    event.setCancelled(true);
                    if (!m.hasFrameRenderer()) {
                        m.createFrameRenderer();
                        this.saveRenderers();
                    }

                    if (p.getGameMode() != GameMode.CREATIVE) {
                        int amount = h.getAmount() - 1;
                        if (amount == 0) {
                            p.setItemInHand(new ItemStack(Material.AIR));
                        } else {
                            h.setAmount(amount);
                        }
                    }

                    i.setItem(m.getFrameRenderer().createMap());
                }
            }
        }

    }

    @EventHandler(
            priority = EventPriority.NORMAL
    )
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.renderers.handleQuit(event.getPlayer());
    }
}
