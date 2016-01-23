package us.mcmagic.parkmanager.pixelator.renderer.types;

import org.bukkit.entity.Player;
import us.mcmagic.parkmanager.ParkManager;
import us.mcmagic.parkmanager.pixelator.renderer.ImageRenderer;
import us.mcmagic.parkmanager.pixelator.renderer.SourceType;
import us.mcmagic.parkmanager.pixelator.renderer.util.ImageUtil;
import us.mcmagic.parkmanager.pixelator.util.FilesystemCache;

import java.awt.image.BufferedImage;
import java.util.concurrent.atomic.AtomicBoolean;

public class MapImageRenderer extends ImageRenderer {

    private final AtomicBoolean loaded = new AtomicBoolean(false);
    private String imageSource;
    private SourceType imageSourceType;
    private FrameImageRenderer frameRenderer;


    public MapImageRenderer(ParkManager plugin, short id, final String imageSource, final SourceType imageSourceType) throws Exception {
        super(id, null);
        this.imageSource = imageSource;
        this.imageSourceType = imageSourceType;
        loadNew();
    }

    public void loadNew() {
        FilesystemCache.setLoader(id, new FilesystemCache.Loader(this) {
            @Override
            public BufferedImage load() {
                MapImageRenderer.this.image = imageSourceType.loadImage(imageSource);
                if (MapImageRenderer.this.image == null) {
                    throw new NullPointerException("The source does not contain an image");
                } else {
                    MapImageRenderer.this.image = ImageUtil.scale(MapImageRenderer.this.image, 128, 128);
                    MapImageRenderer.this.xCap = MapImageRenderer.this.image.getWidth(null);
                    MapImageRenderer.this.yCap = MapImageRenderer.this.image.getHeight(null);
                }
                return MapImageRenderer.this.image;
            }
        });
    }

    public MapImageRenderer(ParkManager plugin, String imageSource, SourceType imageSourceType) throws Exception {
        this(plugin, generateMapId(), imageSource, imageSourceType);
    }

    private MapImageRenderer(final ParkManager plugin, short id, final short frameId, final String imageSource, final SourceType imageSourceType) throws Exception {
        super(id, null);
        this.imageSource = imageSource;
        this.imageSourceType = imageSourceType;
        loadNew();
        MapImageRenderer.this.createFrameRenderer(frameId);
    }

    public static MapImageRenderer load(short id, String type) throws Exception {
        return new MapImageRenderer(ParkManager.getInstance(), id, type, SourceType.URL);
    }

    public static MapImageRenderer fromString(ParkManager plugin, String s) throws Exception {
        String[] e = s.split("@");
        return e.length == 4 ? new MapImageRenderer(plugin, Short.parseShort(e[0]), Short.parseShort(e[3]), e[1],
                SourceType.valueOf(e[2])) : new MapImageRenderer(plugin, Short.parseShort(e[0]), e[1], SourceType.valueOf(e[2]));
    }

    public void deactivate() {
        super.deactivate();
        if (this.hasFrameRenderer()) {
            this.frameRenderer.deactivate();
        }

    }

    public void handleQuit(Player p) {
        super.handleQuit(p);
        if (this.hasFrameRenderer()) {
            this.frameRenderer.handleQuit(p);
        }

    }

    private void createFrameRenderer(short id) {
        if (this.hasFrameRenderer()) {
            throw new UnsupportedOperationException("There is already a frame image renderer");
        } else {
            this.frameRenderer = new FrameImageRenderer(id, this.image, this.oX, this.oY, this.xCap, this.yCap);
        }
    }

    public void createFrameRenderer() {
        this.createFrameRenderer(generateMapId());
    }

    public String getImageSource() {
        return this.imageSource;
    }

    public SourceType getImageSourceType() {
        return this.imageSourceType;
    }

    public FrameImageRenderer getFrameRenderer() {
        return this.frameRenderer;
    }

    public boolean hasFrameRenderer() {
        return this.frameRenderer != null;
    }

    public String toString() {
        return super.toString() + "@" + this.imageSource + "@" + this.imageSourceType.name() + (this.hasFrameRenderer() ? "@" + this.frameRenderer.getId() : "");
    }
}
