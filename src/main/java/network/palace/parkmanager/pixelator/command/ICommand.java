package network.palace.parkmanager.pixelator.command;

import network.palace.parkmanager.ParkManager;
import org.bukkit.command.CommandSender;

public interface ICommand {

    void execute(ParkManager var1, CommandSender var2, String[] var3);
}
