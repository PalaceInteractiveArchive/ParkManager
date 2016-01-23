package us.mcmagic.parkmanager.pixelator.command;

import org.bukkit.command.CommandSender;
import us.mcmagic.parkmanager.ParkManager;

public interface ICommand {

    void execute(ParkManager var1, CommandSender var2, String[] var3);
}
