package us.mcmagic.parkmanager.pixelator.util;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.apache.commons.io.IOUtils;
import org.bukkit.map.MapPalette;
import us.mcmagic.parkmanager.ParkManager;
import us.mcmagic.parkmanager.pixelator.renderer.ImageRenderer;

import java.awt.image.BufferedImage;
import java.io.*;

public class FilesystemCache {

    private static final Cache<Short, Loader> LOADERS = CacheBuilder.newBuilder().build();
    private static final LoadingCache<ImageRenderer, byte[]> GENERATED_PIXELS = CacheBuilder.newBuilder().build(new CacheLoader<ImageRenderer, byte[]>() {
        @SuppressWarnings("deprecation")
        @Override
        public byte[] load(ImageRenderer renderer) throws Exception {
            File file = new File(ParkManager.getInstance().getDataFolder().getPath() + File.separator + "cache", renderer.getId() + ".cache");
            if (file.exists()) {
                byte[] data = IOUtils.toByteArray(new FileInputStream(file));
                DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(data));
                renderer.oX = inputStream.readInt();
                renderer.oY = inputStream.readInt();
                renderer.xCap = inputStream.readInt();
                renderer.yCap = inputStream.readInt();
                byte[] out = new byte[data.length - 16];
                inputStream.readFully(out);
                inputStream.close();
                return out;
            } else {
                Loader loader = LOADERS.getIfPresent(renderer.getId());
                if (loader == null) {
                    throw new RuntimeException("Null loader for ID (" + renderer.getId() + ")");
                } else {
                    BufferedImage image = loader.load();
                    DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(file));
                    dataOutputStream.writeInt(renderer.oX);
                    dataOutputStream.writeInt(renderer.oY);
                    dataOutputStream.writeInt(renderer.xCap);
                    dataOutputStream.writeInt(renderer.yCap);
                    byte[] data = MapPalette.imageToBytes(image);
                    dataOutputStream.write(data);
                    dataOutputStream.close();
                    return data;
                }
            }
        }
    });

    public static void setLoader(short id, Loader loader) {
        LOADERS.put(id, loader);
        getByteData(loader.renderer);
    }

    public static byte[] getByteData(ImageRenderer renderer) {
        return GENERATED_PIXELS.getUnchecked(renderer);
    }

    public static abstract class Loader {
        protected final ImageRenderer renderer;

        public Loader(ImageRenderer renderer) {
            this.renderer = renderer;
        }

        public abstract BufferedImage load();
    }
}
