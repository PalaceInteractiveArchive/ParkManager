package network.palace.parkmanager.commands.shop;

import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.parkmanager.commands.shop.item.AddCommand;
import network.palace.parkmanager.commands.shop.item.ListCommand;
import network.palace.parkmanager.commands.shop.item.RemoveCommand;

@CommandMeta(description = "Manage shop items")
public class ItemCommand extends CoreCommand {

    public ItemCommand() {
        super("item");
        registerSubCommand(new AddCommand());
        registerSubCommand(new ListCommand());
        registerSubCommand(new RemoveCommand());
    }

    @Override
    protected boolean isUsingSubCommandsOnly() {
        return true;
    }
}
