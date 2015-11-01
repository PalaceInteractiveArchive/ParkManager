package us.mcmagic.magicassistant.show.actions;

import com.sk89q.worldedit.EmptyClipboardException;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.data.DataException;
import com.sk89q.worldedit.util.io.file.FilenameException;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import us.mcmagic.magicassistant.show.Show;
import us.mcmagic.magicassistant.show.TerrainManager;

import java.io.File;
import java.io.IOException;

public class SchematicAction extends ShowAction {
    private static WorldEditPlugin wep = (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit");
    private static TerrainManager tm = new TerrainManager(wep, Bukkit.getWorlds().get(0));
    private Location loc;
    private File fname;
    private boolean noAir;

    public SchematicAction(Show show, long time, Location loadloc, File file, boolean pastea) {
        super(show, time);
        loc = loadloc;
        fname = file;
        noAir = pastea;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void play() {
        try {
            tm.loadSchematic(wep, fname, loc, noAir);
        } catch (FilenameException | DataException | MaxChangedBlocksException | IOException | EmptyClipboardException e) {
            e.printStackTrace();
        }
    }


}