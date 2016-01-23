package us.mcmagic.parkmanager.designstation;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import us.mcmagic.parkmanager.ParkManager;

/**
 * Created by LukeSmalley on 5/18/2015.
 */
public class DesignStationClick {

    public static void handleModel(InventoryClickEvent event) {
        ItemStack clickedItem = event.getInventory().getItem(event.getSlot());
        Player player = (Player) event.getWhoClicked();
        TestTrackVehicle vehicle = DesignStation.getPlayerVehicle(player.getUniqueId());

        if (itemEquals(clickedItem, DesignStation.createCar)) {
            if (vehicle.type != TestTrackVehicle.carType) {
                vehicle.height = 0;
                vehicle.width = 0;
                vehicle.engineType = TestTrackVehicle.EngineType.GASOLINE;
            }
            vehicle.type = TestTrackVehicle.carType;
            DesignStation.openPickSizeAndColorInventory(player);
        } else if (itemEquals(clickedItem, DesignStation.createTruck)) {
            if (vehicle.type != TestTrackVehicle.truckType) {
                vehicle.height = 0;
                vehicle.width = 0;
                vehicle.engineType = TestTrackVehicle.EngineType.GASOLINE;
            }
            vehicle.type = TestTrackVehicle.truckType;
            DesignStation.openPickSizeAndColorInventory(player);
        } else if (itemEquals(clickedItem, DesignStation.createSmartcar)) {
            if (vehicle.type != TestTrackVehicle.ecoCarType) {
                vehicle.height = 0;
                vehicle.width = 0;
                vehicle.engineType = TestTrackVehicle.EngineType.GASOLINE;
            }
            vehicle.type = TestTrackVehicle.ecoCarType;
            DesignStation.openPickSizeAndColorInventory(player);
        }
    }

    public static void handleSizeAndColor(InventoryClickEvent event) {
        ItemStack clickedItem = event.getCurrentItem();
        Player player = (Player) event.getWhoClicked();
        TestTrackVehicle vehicle = DesignStation.getPlayerVehicle(player.getUniqueId());

        if (itemEquals(clickedItem, DesignStation.nextButton)) {
            DesignStation.openPickEngineInventory(player);
            return;
        } else if (itemEquals(clickedItem, DesignStation.backButton)) {
            DesignStation.openPickModelInventory(player);
            return;
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
            if (vehicle.width > -vehicle.getWidthOffset()) {
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
        }

        event.getInventory().setItem(31, DesignStation.getPlayerVehicleItem(player.getUniqueId()));
    }

    public static void handleEngine(InventoryClickEvent event) {
        ItemStack clickedItem = event.getInventory().getItem(event.getSlot());
        final Player player = (Player) event.getWhoClicked();
        TestTrackVehicle vehicle = DesignStation.getPlayerVehicle(player.getUniqueId());

        boolean exitDesigner = true;

        if (itemEquals(clickedItem, DesignStation.backButton)) {
            DesignStation.openPickSizeAndColorInventory(player);
            return;
        } else if (itemEquals(clickedItem, DesignStation.solarDriveEngine)) {
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
            Bukkit.getScheduler().runTaskLater(ParkManager.instance, new Runnable() {
                @Override
                public void run() {
                    player.closeInventory();
                    player.sendMessage(ChatColor.GREEN + "You have completed the design process! Please continue to the boarding area to test-drive your vehicle.");
                }
            }, 5L);
        }
    }

    private static boolean itemEquals(ItemStack a, ItemStack b) {
        return a != null && b != null && a.getType() == b.getType() && a.getDurability() == b.getDurability() &&
                a.getItemMeta().hasDisplayName() == b.getItemMeta().hasDisplayName() &&
                a.getItemMeta().getDisplayName().equals(b.getItemMeta().getDisplayName());
    }

}
