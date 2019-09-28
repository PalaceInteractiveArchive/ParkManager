package network.palace.parkmanager.commands;

import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.Rank;
import network.palace.parkmanager.commands.shop.ItemCommand;
import network.palace.parkmanager.commands.shop.OutfitCommand;
import network.palace.parkmanager.commands.shop.*;

@CommandMeta(description = "Shop command", rank = Rank.MOD)
public class ShopCommand extends CoreCommand {

    public ShopCommand() {
        super("shop");
        registerSubCommand(new CreateCommand());
        registerSubCommand(new ListCommand());
        registerSubCommand(new ReloadCommand());
        registerSubCommand(new RemoveCommand());
        registerSubCommand(new ItemCommand());
        registerSubCommand(new OutfitCommand());
    }

    @Override
    protected boolean isUsingSubCommandsOnly() {
        return true;
    }
}
