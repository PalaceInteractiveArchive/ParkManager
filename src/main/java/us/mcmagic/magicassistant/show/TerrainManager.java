package us.mcmagic.magicassistant.show;

import com.sk89q.worldedit.*;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.data.DataException;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.schematic.SchematicFormat;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.util.io.Closer;
import com.sk89q.worldedit.util.io.file.FilenameException;
import com.sk89q.worldedit.world.registry.WorldData;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@SuppressWarnings("deprecation")
public class TerrainManager {
    private final WorldEdit we;
    private final LocalSession localSession;
    private final EditSession editSession;
    private final LocalPlayer localPlayer;

    /**
     * Constructor
     *
     * @param wep    the WorldEdit plugin instance
     * @param player the player to work with
     */
    public TerrainManager(WorldEditPlugin wep, Player player) {
        we = wep.getWorldEdit();
        localPlayer = wep.wrapPlayer(player);
        localSession = we.getSession(localPlayer);
        editSession = localSession.createEditSession(localPlayer);
    }

    /**
     * Constructor
     *
     * @param wep   the WorldEdit plugin instance
     * @param world the world to work in
     */
    public TerrainManager(WorldEditPlugin wep, World world) {
        we = wep.getWorldEdit();
        localPlayer = null;
        localSession = new LocalSession(we.getConfiguration());
        editSession = new EditSession(new BukkitWorld(world), we.getConfiguration().maxChangeLimit);
    }

    /**
     * Write the terrain bounded by the given locations to the given file as a MCedit format
     * schematic.
     *
     * @param saveFile a File representing the schematic file to create
     * @param l1       one corner of the region to save
     * @param l2       the corner of the region to save, opposite to l1
     * @throws com.sk89q.worldedit.data.DataException
     * @throws java.io.IOException
     */
    public void saveTerrain(File saveFile, Location l1, Location l2) throws FilenameException, DataException, IOException {
        Vector min = getMin(l1, l2);
        Vector max = getMax(l1, l2);

        saveFile = we.getSafeSaveFile(localPlayer, saveFile.getParentFile(), saveFile.getName(), ".schematic", ".schematic");

        editSession.enableQueue();
        CuboidClipboard clipboard = new CuboidClipboard(max.subtract(min).add(new Vector(1, 1, 1)), min);
        clipboard.copy(editSession);
        SchematicFormat.MCEDIT.save(clipboard, saveFile);
        editSession.flushQueue();
    }

    public void loadSchematic(WorldEditPlugin wep, File saveFile, Location loc, boolean noAir) throws FilenameException, DataException,
            IOException, MaxChangedBlocksException, EmptyClipboardException {
        File f = wep.getWorldEdit().getSafeOpenFile(null, new File("plugins/WorldEdit/schematics"), saveFile.getName(),
                "schematic", "schematic");
        Closer closer = Closer.create();
        FileInputStream fis = closer.register(new FileInputStream(f));
        BufferedInputStream bis = closer.register(new BufferedInputStream(fis));
        ClipboardReader reader = ClipboardFormat.SCHEMATIC.getReader(bis);
        WorldData worldData = new BukkitWorld(loc.getWorld()).getWorldData();
        Clipboard clipboard = reader.read(worldData);
        localSession.setClipboard(new ClipboardHolder(clipboard, worldData));
        Region region = clipboard.getRegion();
        Vector to = clipboard.getOrigin();
        Operation operation = localSession.getClipboard().createPaste(editSession, editSession.getWorld()
                .getWorldData()).to(to).ignoreAirBlocks(noAir).build();
        Operations.completeLegacy(operation);
        /*

        Location loc1 = null;
        Location loc2 = null;
        CuboidRegion r = new CuboidRegion(getMin(loc1, loc2), getMax(loc1, loc2));
        BlockArrayClipboard clip = new BlockArrayClipboard(r);
        ClipboardHolder holder = new ClipboardHolder(clip, new BukkitWorld(loc.getWorld()).getWorldData());
        localSession.setClipboard(holder);
        localSession.getClipboard().place(editSession, getPastePosition(loc), noAir);
        editSession.flushQueue();
        we.flushBlockBag(localPlayer, editSession);
        */
    }

    public void loadSchematic(WorldEditPlugin wep, File saveFile, boolean noAir) throws FilenameException, DataException, IOException,
            MaxChangedBlocksException, EmptyClipboardException {
        loadSchematic(wep, saveFile, null, noAir);
    }

    private Vector getMin(Location l1, Location l2) {
        return new Vector(Math.min(l1.getBlockX(), l2.getBlockX()), Math.min(l1.getBlockY(), l2.getBlockY()),
                Math.min(l1.getBlockZ(), l2.getBlockZ())
        );
    }

    private Vector getMax(Location l1, Location l2) {
        return new Vector(Math.max(l1.getBlockX(), l2.getBlockX()), Math.max(l1.getBlockY(), l2.getBlockY()),
                Math.max(l1.getBlockZ(), l2.getBlockZ())
        );
    }
}