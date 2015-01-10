package us.mcmagic.magicassistant.show.actions;

import com.sk89q.worldedit.EmptyClipboardException;
import com.sk89q.worldedit.FilenameException;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.data.DataException;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import us.mcmagic.magicassistant.show.Show;
import us.mcmagic.magicassistant.show.TerrainManager;

import java.io.File;
import java.io.IOException;

public class SchematicAction extends ShowAction {
    public Location loc;
    public File fname;
    public World world;
    public boolean noAir;

    public SchematicAction(Show show, long time, Location loadloc, File file, boolean pastea) {
        super(show, time);
        loc = loadloc;
        world = loc.getWorld();
        fname = file;
        noAir = pastea;
    }

    @Override
    public void Play() {
        WorldEditPlugin wep = (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit");
        TerrainManager tm = new TerrainManager(wep, world);
        try {
            tm.loadSchematic(fname, loc, noAir);
        } catch (FilenameException | DataException | IOException | MaxChangedBlocksException | EmptyClipboardException ignored) {
        }
    }


}