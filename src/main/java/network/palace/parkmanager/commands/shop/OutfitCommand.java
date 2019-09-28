package network.palace.parkmanager.commands.shop;

import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.parkmanager.commands.shop.outfit.AddCommand;
import network.palace.parkmanager.commands.shop.outfit.ListCommand;
import network.palace.parkmanager.commands.shop.outfit.RemoveCommand;

@CommandMeta(description = "Manage shop outfits")
public class OutfitCommand extends CoreCommand {

    public OutfitCommand() {
        super("outfit");
        registerSubCommand(new AddCommand());
        registerSubCommand(new ListCommand());
        registerSubCommand(new RemoveCommand());
    }

    @Override
    protected boolean isUsingSubCommandsOnly() {
        return true;
    }
}
