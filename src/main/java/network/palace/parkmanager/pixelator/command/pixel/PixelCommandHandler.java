package network.palace.parkmanager.pixelator.command.pixel;

import network.palace.parkmanager.ParkManager;
import network.palace.parkmanager.pixelator.command.CommandHandler;

public class PixelCommandHandler extends CommandHandler {

    public PixelCommandHandler(ParkManager plugin) {
        super(plugin, "pixel", "§3[§b§lPixelator§3]§r §cCommand help page for Pixelator:",
                "§8§m------------------§8[§7Page <current_page> §7of §6§l<page_amount>§8]§m------------------§r",
                "§a• <command>\n  §7▻ <description>\n  §7▻ Permission: §2<permission>", 5, "Pixelator.*");
    }

    protected void registerCommands() {
        this.register(CreateCommand.class);
        this.register(GiveCommand.class);
        this.register(RemoveCommand.class);
        this.register(ListCommand.class);
        this.register(HelpCommand.class);
    }
}