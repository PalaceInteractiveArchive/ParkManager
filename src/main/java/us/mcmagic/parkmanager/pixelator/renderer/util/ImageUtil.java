package us.mcmagic.parkmanager.pixelator.renderer.util;

import us.mcmagic.parkmanager.pixelator.renderer.SourceType;

import javax.imageio.ImageIO;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;

public abstract class ImageUtil {

    public static BufferedImage loadImage(File f) {
        try {
            return ImageIO.read(f);
        } catch (Exception var2) {
            return null;
        }
    }

    public static BufferedImage loadImage(URL u) {
        try {
            return ImageIO.read(u);
        } catch (Exception var2) {
            return null;
        }
    }

    public static BufferedImage loadImage(String s, SourceType type) {
        if (type == null) {
            return null;
        } else if (type == SourceType.FILE) {
            return loadImage(new File(s));
        } else {
            try {
                return loadImage(new URL(s));
            } catch (Exception var3) {
                return null;
            }
        }
    }

    public static BufferedImage loadImage(String s) {
        return loadImage(s, SourceType.determine(s));
    }

    public static BufferedImage scale(BufferedImage b, int width, int height) {
        if (b.getWidth() == width && b.getHeight() == height) {
            return b;
        } else {
            AffineTransform a = AffineTransform.getScaleInstance((double) width / (double) b.getWidth(), (double) height / (double) b.getHeight());
            AffineTransformOp o = new AffineTransformOp(a, 2);
            return o.filter(b, new BufferedImage(width, height, b.getType()));
        }
    }
}
