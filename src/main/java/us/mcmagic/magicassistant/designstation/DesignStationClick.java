package us.mcmagic.magicassistant.designstation;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import us.mcmagic.magicassistant.MagicAssistant;

/**
 * Created by LukeSmalley on 5/18/2015.
 */
public class DesignStationClick {

    public static void handleModel(InventoryClickEvent event) {
        ItemStack clickedItem = event.getInventory().getItem(event.getSlot());
        Player player = (Player) event.getWhoClicked();
        TestTrackVehicle vehicle = DesignStation.getPlayerVehicle(player.getUniqueId().toString());

        if (itemEquals(clickedItem, DesignStation.createCar)) {
            vehicle.type = TestTrackVehicle.carType;
            DesignStation.openPickSizeAndColorInventory(player);
        } else if (itemEquals(clickedItem, DesignStation.createTruck)) {
            vehicle.type = TestTrackVehicle.truckType;
            DesignStation.openPickSizeAndColorInventory(player);
        } else if (itemEquals(clickedItem, DesignStation.createSmartcar)) {
            vehicle.type = TestTrackVehicle.ecoCarType;
            DesignStation.openPickSizeAndColorInventory(player);
        }
    }

    public static void handleSizeAndColor(InventoryClickEvent event) {
        ItemStack clickedItem = event.getCurrentItem();
        Player player = (Player) event.getWhoClicked();
        TestTrackVehicle vehicle = DesignStation.getPlayerVehicle(player.getUniqueId().toString());
        boolean resetVehicleItem = true;
        if (itemEquals(clickedItem, DesignStation.nextButton)) {
            DesignStation.openPickEngineInventory(player);
            resetVehicleItem = false;
        } else if (itemEquals(clickedItem, DesignStation.backButton)) {
            DesignStation.openPickModelInventory(player);
            resetVehicleItem = false;
        } else if (itemEquals(clickedItem, DesignStation.tallerButton)) {
            if (vehicle.height < 8) {
                vehicle.height++;
            }
        } else if (itemEquals(clickedItem, DesignStation.shorterButton)) {
            if (vehicle.height > 0) {
                vehicle.height--;
            }
        } else if (itemEquals(clickedItem, DesignStation.widerButton)) {
            if (vehicle.width < 8) {
                vehicle.width++;
            }
        } else if (itemEquals(clickedItem, DesignStation.thinnerButton)) {
            if (vehicle.width > -7) {
                vehicle.width--;
            }
        } else if (itemEquals(clickedItem, DesignStation.redButton)) {
            vehicle.color = ChatColor.DARK_RED;
        } else if (itemEquals(clickedItem, DesignStation.darkGreenButton)) {
            vehicle.color = ChatColor.DARK_GREEN;
        } else if (itemEquals(clickedItem, DesignStation.lightGreenButton)) {
            vehicle.color = ChatColor.GREEN;
        } else if (itemEquals(clickedItem, DesignStation.yellowButton)) {
            vehicle.color = ChatColor.YELLOW;
        } else if (itemEquals(clickedItem, DesignStation.purpleButton)) {
            vehicle.color = ChatColor.DARK_PURPLE;
        } else if (itemEquals(clickedItem, DesignStation.magentaButton)) {
            vehicle.color = ChatColor.LIGHT_PURPLE;
        } else if (itemEquals(clickedItem, DesignStation.darkGreyButton)) {
            vehicle.color = ChatColor.DARK_GRAY;
        } else if (itemEquals(clickedItem, DesignStation.lightGreyButton)) {
            vehicle.color = ChatColor.GRAY;
        } else if (itemEquals(clickedItem, DesignStation.whiteButton)) {
            vehicle.color = ChatColor.WHITE;
        } else if (itemEquals(clickedItem, DesignStation.darkBlueButton)) {
            vehicle.color = ChatColor.DARK_BLUE;
        } else if (itemEquals(clickedItem, DesignStation.lightBlueButton)) {
            vehicle.color = ChatColor.BLUE;
        } else if (itemEquals(clickedItem, DesignStation.cyanButton)) {
            vehicle.color = ChatColor.DARK_AQUA;
        } else {
            Bukkit.broadcastMessage("Nothing changed!");
        }
        if (resetVehicleItem) {
            event.getInventory().setItem(31, DesignStation.getPlayerVehicleItem(player.getUniqueId().toString()));
        }
    }

    public static void handleEngine(InventoryClickEvent event) {
        ItemStack clickedItem = event.getInventory().getItem(event.getSlot());
        final Player player = (Player) event.getWhoClicked();
        TestTrackVehicle vehicle = DesignStation.getPlayerVehicle(player.getUniqueId().toString());

        boolean exitDesigner = true;

        if (itemEquals(clickedItem, DesignStation.solarDriveEngine)) {
            vehicle.engineType = TestTrackVehicle.EngineType.SOLAR;
        } else if (itemEquals(clickedItem, DesignStation.fuelCellEngine)) {
            vehicle.engineType = TestTrackVehicle.EngineType.FUELCELL;
        } else if (itemEquals(clickedItem, DesignStation.ecoElectricEngine)) {
            vehicle.engineType = TestTrackVehicle.EngineType.ELECTRIC;
        } else if (itemEquals(clickedItem, DesignStation.evHybridEngine)) {
            vehicle.engineType = TestTrackVehicle.EngineType.HYBRID;
        } else if (itemEquals(clickedItem, DesignStation.gasEngine)) {
            vehicle.engineType = TestTrackVehicle.EngineType.GASOLINE;
        } else if (itemEquals(clickedItem, DesignStation.superChargedEngine)) {
            vehicle.engineType = TestTrackVehicle.EngineType.SUPERCHARGED;
        } else if (itemEquals(clickedItem, DesignStation.plasmaBurnerEngine)) {
            vehicle.engineType = TestTrackVehicle.EngineType.PLASMA;
        } else {
            exitDesigner = false;
        }

        if (exitDesigner) {
            Bukkit.getScheduler().runTaskLater(MagicAssistant.instance, new Runnable() {
                @Override
                public void run() {
                    player.closeInventory();
                    player.sendMessage(ChatColor.GREEN + "You have completed the design process! Please continue to the boarding area to test-drive your vehicle.");
                }
            }, 5L);
        }
    }

    private static boolean itemEquals(ItemStack a, ItemStack b) {
        if (a == null || b == null) {
            return false;
        }
        if (a.getType() != b.getType()) {
            return false;
        }
        if (a.getDurability() != b.getDurability()) {
            return false;
        }
        if (a.getItemMeta().hasDisplayName() != b.getItemMeta().hasDisplayName()) {
            return false;
        }
        if (!a.getItemMeta().getDisplayName().equals(b.getItemMeta().getDisplayName())) {
            return false;
        }
        return true;
    }

}
