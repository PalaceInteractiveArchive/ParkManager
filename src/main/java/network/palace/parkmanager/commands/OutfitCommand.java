package network.palace.parkmanager.commands;

import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.Rank;
import network.palace.parkmanager.commands.outfits.CreateCommand;
import network.palace.parkmanager.commands.outfits.ListCommand;
import network.palace.parkmanager.commands.outfits.ReloadCommand;
import network.palace.parkmanager.commands.outfits.RemoveCommand;

@CommandMeta(description = "Outfit command", rank = Rank.CM)
public class OutfitCommand extends CoreCommand {

    public OutfitCommand() {
        super("outfit");
        registerSubCommand(new CreateCommand());
        registerSubCommand(new ListCommand());
        registerSubCommand(new ReloadCommand());
        registerSubCommand(new RemoveCommand());
    }

    @Override
    protected boolean isUsingSubCommandsOnly() {
        return true;
    }
}
