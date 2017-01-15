package network.palace.parkmanager.pixelator.renderer.types;

import network.palace.parkmanager.pixelator.util.FilesystemCache;
import network.palace.parkmanager.pixelator.renderer.ImageRenderer;

import java.awt.image.BufferedImage;

public class FrameImageRenderer extends ImageRenderer {

    public FrameImageRenderer(short id, final BufferedImage image, int oX, int oY, int xCap, int yCap) {
        super(id, image);
        this.oX = oX;
        this.oY = oY;
        this.xCap = xCap;
        this.yCap = yCap;
        FilesystemCache.setLoader(id, new FilesystemCache.Loader(this) {
            @Override
            public BufferedImage load() {
                return image;
            }
        });
        getCache(null);
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }
}
