package us.mcmagic.parkmanager.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.mcmagic.mcmagiccore.MCMagicCore;
import us.mcmagic.parkmanager.ParkManager;
import us.mcmagic.parkmanager.handlers.HotelRoom;
import us.mcmagic.parkmanager.handlers.Warp;
import us.mcmagic.parkmanager.utils.PlayerUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Greenlock28 on 1/23/2015.
 */
public class Commandhotel implements CommandExecutor {
    private static List<String> usersNeedingConfirmationForClear = new ArrayList<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.isOp()) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to perform this command!");
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "/hotel <reload|list|clear|remove|vacate|occupy|gift|setwarp|info|extendtime|settime>");
            return true;
        }

        switch (args[0]) {
            case "confirm":
                execute_confirm(sender, args);
                return true;
            case "reload":
                execute_reload(sender, args);
                return true;
            case "list":
                execute_list(sender, args);
                return true;
            case "clear":
                execute_clear(sender, args, false);
                return true;
            case "remove":
                execute_remove(sender, args);
                return true;
            case "vacate":
                execute_vacate(sender, args);
                return true;
            case "occupy":
                execute_occupy(sender, args);
                return true;
            case "gift":
                execute_gift(sender, args);
                return true;
            case "setwarp":
                execute_setwarp(sender, args);
                return true;
            case "info":
                execute_info(sender, args);
                return true;
            case "extendtime":
                execute_extendtime(sender, args);
                return true;
            case "settime":
                execute_settime(sender, args);
        }
        return true;
    }

    private static void execute_confirm(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players may use this command!");
            return;
        }
        Player player = (Player) sender;
        if (usersNeedingConfirmationForClear.contains(player.getUniqueId().toString())) {
            usersNeedingConfirmationForClear.remove(player.getUniqueId().toString());
            execute_clear(sender, args, true);
        }
    }

    private static void execute_reload(CommandSender sender, String[] args) {
        sender.sendMessage(ChatColor.BLUE + "Reloading hotel rooms...");
        ParkManager.hotelManager.refreshRooms();
        sender.sendMessage(ChatColor.BLUE + "Hotel rooms reloaded!");
    }

    private static void execute_list(CommandSender sender, String[] args) {
        List<HotelRoom> rooms = ParkManager.hotelManager.getHotelRooms();
        if (rooms.isEmpty()) {
            sender.sendMessage(ChatColor.BLUE + "No hotel rooms exist!");
            return;
        }
        String roomList = ChatColor.BLUE + "Hotel Rooms: ";
        for (HotelRoom room : rooms) {
            roomList += room.getName() + ", ";
        }
        roomList = roomList.substring(0, roomList.lastIndexOf(","));
        sender.sendMessage(roomList);
    }

    private static void execute_clear(CommandSender sender, String[] args, boolean confirmed) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players may use this command!");
            return;
        }
        Player player = (Player) sender;
        if (!confirmed) {
            player.sendMessage(ChatColor.GREEN + "" + ChatColor.UNDERLINE + "This is a dangerous command! Use /hotel confirm to confirm this action.  (Use /hotel cancel to cancel this action.)");
            usersNeedingConfirmationForClear.add(player.getUniqueId().toString());
            return;
        }

        List<HotelRoom> rooms = ParkManager.hotelManager.getHotelRooms();
        if (rooms.isEmpty()) {
            sender.sendMessage(ChatColor.BLUE + "No hotel rooms to clear!");
            return;
        }
        for (HotelRoom room : rooms) {
            ParkManager.hotelManager.removeRoom(room);
        }
        ParkManager.hotelManager.updateRooms();
        sender.sendMessage(ChatColor.BLUE + "Hotel rooms have been cleared!");
    }

    private static void execute_remove(CommandSender sender, String[] args) {
        List<String> tweakedArgs = getAdvancedArguments(args);

        if (tweakedArgs.size() < 2) {
            sender.sendMessage(ChatColor.RED + "Not enough arguments were provided!");
            sender.sendMessage(ChatColor.RED + "Usage: /hotel remove <roomName>");
            sender.sendMessage(ChatColor.RED + "This command supports the use of \" and ' to include spaces in command arguments.");
            return;
        }

        if (ParkManager.hotelManager.getRoom(tweakedArgs.get(1)) == null) {
            sender.sendMessage(ChatColor.RED + "The specified room does not exist!");
            return;
        }

        ParkManager.hotelManager.removeRoom(ParkManager.hotelManager.getRoom(tweakedArgs.get(1)));
        ParkManager.hotelManager.refreshRooms();
        ParkManager.hotelManager.updateRooms();

        sender.sendMessage(ChatColor.BLUE + "Room \"" + tweakedArgs.get(1) + "\" has been removed!");
    }

    private static void execute_vacate(CommandSender sender, String[] args) {
        List<String> tweakedArgs = getAdvancedArguments(args);

        if (tweakedArgs.size() < 2) {
            sender.sendMessage(ChatColor.RED + "Not enough arguments were provided!");
            sender.sendMessage(ChatColor.RED + "Usage: /hotel vacate <roomName>");
            sender.sendMessage(ChatColor.RED + "This command supports the use of \" and ' to include spaces in command arguments.");
            return;
        }

        HotelRoom room = ParkManager.hotelManager.getRoom(tweakedArgs.get(1));
        if (room == null) {
            sender.sendMessage(ChatColor.RED + "The specified room does not exist!");
            return;
        }
        room.setCurrentOccupant(null);
        room.setCheckoutTime(0);
        ParkManager.hotelManager.updateHotelRoom(room);
        ParkManager.hotelManager.updateRooms();

        sender.sendMessage(ChatColor.BLUE + "Room \"" + tweakedArgs.get(1) + "\" has been vacated!");
    }

    private static void execute_occupy(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players may use this command!");
            return;
        }
        Player player = (Player) sender;

        List<String> tweakedArgs = getAdvancedArguments(args);

        if (tweakedArgs.size() < 2) {
            sender.sendMessage(ChatColor.RED + "Not enough arguments were provided!");
            sender.sendMessage(ChatColor.RED + "Usage: /hotel occupy <roomName>");
            sender.sendMessage(ChatColor.RED + "This command supports the use of \" and ' to include spaces in command arguments.");
            return;
        }

        HotelRoom room = ParkManager.hotelManager.getRoom(tweakedArgs.get(1));
        if (room == null) {
            sender.sendMessage(ChatColor.RED + "The specified room does not exist!");
            return;
        }
        room.setCurrentOccupant(player.getUniqueId());
        room.setCheckoutTime(72);
        ParkManager.hotelManager.updateHotelRoom(room);
        ParkManager.hotelManager.updateRooms();

        sender.sendMessage(ChatColor.BLUE + "You are now occupying room \"" + tweakedArgs.get(1) + "\"!");
    }

    private static void execute_gift(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players may use this command!");
            return;
        }
        Player player = (Player) sender;

        List<String> tweakedArgs = getAdvancedArguments(args);

        if (tweakedArgs.size() < 3) {
            sender.sendMessage(ChatColor.RED + "Not enough arguments were provided!");
            sender.sendMessage(ChatColor.RED + "Usage: /hotel gift <roomName> <player>");
            sender.sendMessage(ChatColor.RED + "This command supports the use of \" and ' to include spaces in command arguments.");
            return;
        }

        //Player targetPlayer = PlayerUtil.findPlayer(tweakedArgs.get(2));
        String targetPlayer = PlayerUtil.getUUIDFromName(tweakedArgs.get(2));
        if (targetPlayer == null) {
            //sender.sendMessage(ChatColor.RED + "Players may only be gifted hotel rooms when they are online!");
            sender.sendMessage(ChatColor.RED + "Player was not found!");
            return;
        }

        HotelRoom room = ParkManager.hotelManager.getRoom(tweakedArgs.get(1));
        if (room == null) {
            sender.sendMessage(ChatColor.RED + "The specified room does not exist!");
            return;
        }
        //room.setCurrentOccupant(targetPlayer.getUniqueId().toString());
        room.setCurrentOccupant(UUID.fromString(targetPlayer));
        room.setCheckoutTime(72);
        ParkManager.hotelManager.updateHotelRoom(room);
        ParkManager.hotelManager.updateRooms();

        sender.sendMessage(ChatColor.BLUE + "You gifted room \"" + tweakedArgs.get(1) + "\" to " + tweakedArgs.get(2) + "!");
    }

    private static void execute_setwarp(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players may use this command!");
            return;
        }
        Player player = (Player) sender;

        List<String> tweakedArgs = getAdvancedArguments(args);

        if (tweakedArgs.size() < 2) {
            sender.sendMessage(ChatColor.RED + "Not enough arguments were provided!");
            sender.sendMessage(ChatColor.RED + "Usage: /hotel setwarp <roomName>");
            sender.sendMessage(ChatColor.RED + "This command supports the use of \" and ' to include spaces in command arguments.");
            return;
        }

        HotelRoom room = ParkManager.hotelManager.getRoom(tweakedArgs.get(1));
        if (room == null) {
            sender.sendMessage(ChatColor.RED + "The specified room does not exist!");
            return;
        }
        Warp roomWarp = new Warp(tweakedArgs.get(1).replace(" ", ""), MCMagicCore.getMCMagicConfig().serverName, player.getLocation().getX(),
                player.getLocation().getY(), player.getLocation().getZ(), player.getLocation().getYaw(),
                player.getLocation().getPitch(), player.getLocation().getWorld().getName());
        room.setWarp(roomWarp);
        ParkManager.hotelManager.updateHotelRoom(room);
        ParkManager.hotelManager.updateRooms();

        sender.sendMessage(ChatColor.BLUE + "You set the warp for room \"" + tweakedArgs.get(1) + "\"!");
    }

    private static void execute_info(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players may use this command!");
            return;
        }
        Player player = (Player) sender;

        List<String> tweakedArgs = getAdvancedArguments(args);

        if (tweakedArgs.size() < 2) {
            sender.sendMessage(ChatColor.RED + "Not enough arguments were provided!");
            sender.sendMessage(ChatColor.RED + "Usage: /hotel info <roomName>");
            sender.sendMessage(ChatColor.RED + "This command supports the use of \" and ' to include spaces in command arguments.");
            return;
        }

        HotelRoom room = ParkManager.hotelManager.getRoom(tweakedArgs.get(1));
        if (room == null) {
            sender.sendMessage(ChatColor.RED + "The specified room does not exist!");
            return;
        }

        sender.sendMessage(ChatColor.BLUE + "--- " + room.getName() + " ---");
        sender.sendMessage(ChatColor.YELLOW + "Hotel Name: " + room.getHotelName());
        sender.sendMessage(ChatColor.YELLOW + "Room Number: " + Integer.toString(room.getRoomNumber()));
        sender.sendMessage(ChatColor.YELLOW + "Is Occupied: " + Boolean.toString(room.isOccupied()));
        if (room.isOccupied()) {
            sender.sendMessage(ChatColor.YELLOW + "Current Occupant: " + room.getCurrentOccupant() + " (" +
                    PlayerUtil.getNameFromUUID(room.getCurrentOccupant()) + ")");
        }
        sender.sendMessage(ChatColor.YELLOW + "Occupation Cooldown (Hours Left + 1): " + Long.toString(room.getCheckoutTime()));
        sender.sendMessage(ChatColor.YELLOW + "Cost (Coins): " + Integer.toString(room.getCost()));
        sender.sendMessage(ChatColor.YELLOW + "Checkout Notification Recipient: " + (room.getCheckoutNotificationRecipient() == null ? "None" : room.getCheckoutNotificationRecipient()));
    }

    private static void execute_extendtime(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players may use this command!");
            return;
        }
        Player player = (Player) sender;

        List<String> tweakedArgs = getAdvancedArguments(args);

        if (tweakedArgs.size() < 3) {
            sender.sendMessage(ChatColor.RED + "Not enough arguments were provided!");
            sender.sendMessage(ChatColor.RED + "Usage: /hotel extendtime <roomName> <time>");
            sender.sendMessage(ChatColor.RED + "This command supports the use of \" and ' to include spaces in command arguments.");
            return;
        }

        HotelRoom room = ParkManager.hotelManager.getRoom(tweakedArgs.get(1));
        if (room == null) {
            sender.sendMessage(ChatColor.RED + "The specified room does not exist!");
            return;
        }
        room.setCheckoutTime(room.getCheckoutTime() + Integer.parseInt(tweakedArgs.get(2)));
        ParkManager.hotelManager.updateHotelRoom(room);
        ParkManager.hotelManager.updateRooms();

        sender.sendMessage(ChatColor.BLUE + "You extended the reservation time for the room \"" + tweakedArgs.get(1) + "\"!");
    }

    private static void execute_settime(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players may use this command!");
            return;
        }
        Player player = (Player) sender;

        List<String> tweakedArgs = getAdvancedArguments(args);

        if (tweakedArgs.size() < 3) {
            sender.sendMessage(ChatColor.RED + "Not enough arguments were provided!");
            sender.sendMessage(ChatColor.RED + "Usage: /hotel extendtime <roomName> <time>");
            sender.sendMessage(ChatColor.RED + "This command supports the use of \" and ' to include spaces in command arguments.");
            return;
        }

        HotelRoom room = ParkManager.hotelManager.getRoom(tweakedArgs.get(1));
        if (room == null) {
            sender.sendMessage(ChatColor.RED + "The specified room does not exist!");
            return;
        }
        room.setCheckoutTime(Integer.parseInt(tweakedArgs.get(2)));
        ParkManager.hotelManager.updateHotelRoom(room);
        ParkManager.hotelManager.updateRooms();

        sender.sendMessage(ChatColor.BLUE + "You extended the reservation time for the room \"" + tweakedArgs.get(1) + "\"!");
    }


    private static List<String> getAdvancedArguments(String[] args) {
        String squish = "";
        for (String arg : args) {
            squish += arg + " ";
        }
        squish = squish.trim();

        List<String> tweakedArgs = new ArrayList<>();
        String currentArg = "";
        boolean literal = false;
        char literalChar = '0';

        for (int i = 0; i < squish.trim().toCharArray().length; i++) {
            char c = squish.trim().toCharArray()[i];
            if (literal) {
                if (c == literalChar) {
                    if (currentArg.length() > 0) {
                        tweakedArgs.add(currentArg);
                        currentArg = "";
                    }
                    literal = false;
                } else if (c == '\\') {
                    currentArg += squish.trim().toCharArray()[i + 1];
                    i++;
                } else {
                    currentArg += c;
                }
            } else {
                if (c == '"' || c == '\'') {
                    literalChar = c;
                    literal = true;
                } else if (c == ' ') {
                    if (currentArg.length() > 0) {
                        tweakedArgs.add(currentArg);
                        currentArg = "";
                    }
                } else {
                    currentArg += c;
                }
            }
        }

        if (currentArg.length() > 0) {
            tweakedArgs.add(currentArg);
        }

        return tweakedArgs;
    }
}
