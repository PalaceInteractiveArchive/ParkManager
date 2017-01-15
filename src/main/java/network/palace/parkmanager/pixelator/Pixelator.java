package network.palace.parkmanager.pixelator;

import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.pixelator.command.pixel.PixelCommandHandler;
import network.palace.parkmanager.pixelator.renderer.RendererManager;
import org.bukkit.Bukkit;

public class Pixelator {
    public static final String PREFIX = "§3[§b§lPixelator§3]§r ";
    private static Pixelator instance;
    public RendererManager rendererManager;
    public PixelCommandHandler pixelCommandHandler;

    public Pixelator() {
        long check = System.currentTimeMillis();
        instance = this;
        rendererManager = new RendererManager(ParkManager.getInstance());
        pixelCommandHandler = new PixelCommandHandler(ParkManager.getInstance());
        check = System.currentTimeMillis() - check;
        Bukkit.getLogger().info("Pixelator activated! (" + check + " ms)");
    }

    public static Pixelator getInstance() {
        return instance;
    }
}
