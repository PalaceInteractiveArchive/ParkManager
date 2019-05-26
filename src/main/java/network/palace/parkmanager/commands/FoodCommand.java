package network.palace.parkmanager.commands;

import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.Rank;
import network.palace.parkmanager.commands.food.CreateCommand;
import network.palace.parkmanager.commands.food.ListCommand;
import network.palace.parkmanager.commands.food.RemoveCommand;

@CommandMeta(description = "Food location command", rank = Rank.MOD)
public class FoodCommand extends CoreCommand {

    public FoodCommand() {
        super("food");
        registerSubCommand(new CreateCommand());
        registerSubCommand(new ListCommand());
        registerSubCommand(new RemoveCommand());
    }

    @Override
    protected boolean isUsingSubCommandsOnly() {
        return true;
    }
}
