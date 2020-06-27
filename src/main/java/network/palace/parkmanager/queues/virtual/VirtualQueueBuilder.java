package network.palace.parkmanager.queues.virtual;

import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import network.palace.core.utils.MiscUtil;
import network.palace.parkmanager.ParkManager;
import org.bukkit.ChatColor;

public class VirtualQueueBuilder extends VirtualQueue {

    public VirtualQueueBuilder() {
        super(null, null, 0, null, null);
    }

    public void nextStep(CPlayer player, String[] args) {
        if (id == null) {
            //Step 0
            if (args.length < 1) {
                player.sendMessage(ChatColor.RED + "/vqueue create [id]");
                return;
            }
            if (ParkManager.getVirtualQueueManager().getQueueById(args[0]) != null) {
                player.sendMessage(ChatColor.RED + "This id is already used by another queue! Try again: " + ChatColor.YELLOW + "/vqueue create [id]");
                player.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.ITALIC + "See current queue ids with: " + ChatColor.YELLOW + "/vqueue list");
                return;
            }
            this.id = args[0];
            player.sendMessage(ChatColor.GREEN + "Great! Now, let's give your queue a display name. Run " + ChatColor.YELLOW + "/vqueue create [name]");
            player.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.ITALIC + "This name supports color codes! For example, '&aExample &dQueue' becomes '"
                    + ChatColor.GREEN + "Example " + ChatColor.LIGHT_PURPLE + "Queue" + ChatColor.DARK_AQUA + "'.");
            return;
        }
        if (name == null) {
            //Step 1
            if (args.length < 1) {
                player.sendMessage(ChatColor.RED + "/vqueue create [name]");
                return;
            }
            StringBuilder name = new StringBuilder();
            for (int i = 0; i < args.length; i++) {
                name.append(args[i]);
                if (i < (args.length - 1)) {
                    name.append(" ");
                }
            }
            this.name = ChatColor.translateAlternateColorCodes('&', name.toString());
            player.sendMessage(ChatColor.GREEN + "Next, let's define the holding area for this virtual queue. Run " + ChatColor.YELLOW + "/vqueue create [holdingAreaSize]");
            player.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.ITALIC + "The holding area is the number of players in the front of the line that are brought over to the server hosting the queue, before they're admitted.");
            return;
        }
        if (holdingArea == 0) {
            //Step 2
            if (args.length < 1 || !MiscUtil.checkIfInt(args[0]) || Integer.parseInt(args[0]) < 1) {
                player.sendMessage(ChatColor.RED + "/vqueue create [holdingAreaSize]");
                return;
            }
            this.holdingArea = Integer.parseInt(args[0]);
            player.sendMessage(ChatColor.GREEN + "Now, let's define where the \"holding area\" is. Run "
                    + ChatColor.YELLOW + "/vqueue create");
            player.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.ITALIC + "This command doesn't have any parameters because it uses where you're standing when you run it!");
            player.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.ITALIC + "When running this, make sure you're in the exact position you want players to be teleported to.");
            player.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + "Note: " + ChatColor.DARK_AQUA + "" + ChatColor.ITALIC
                    + "Don't forget about where you're looking! Players will be looking exactly where you are when they're teleported in.");
            return;
        }
        if (holdingAreaLocation == null) {
            //Step 5
            this.holdingAreaLocation = player.getLocation();
            player.sendMessage(ChatColor.GREEN + "Now we need to define where players are teleported when they reach the end of the queue. Run "
                    + ChatColor.YELLOW + "/vqueue create");
            player.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.ITALIC + "This command doesn't have any parameters because it uses where you're standing when you run it!");
            player.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.ITALIC + "When running this, make sure you're in the exact position you want players to be teleported to.");
            player.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + "Note: " + ChatColor.DARK_AQUA + "" + ChatColor.ITALIC
                    + "Don't forget about where you're looking! Players will be looking exactly where you are when they're teleported in.");
            return;
        }
        if (queueLocation == null) {
            //Step 6
            this.queueLocation = player.getLocation();
            player.sendMessage(ChatColor.YELLOW + "Great! Finalizing your Virtual Queue...");
            ParkManager.getVirtualQueueManager().addQueue(new VirtualQueue(this.id, this.name, this.holdingArea, this.holdingAreaLocation, Core.getInstanceName()));
            player.sendMessage(ChatColor.GREEN + "Your queue is all ready to go! It's closed by default, but you can change that with " + ChatColor.YELLOW + "/vqueue open");
        }
    }
}
