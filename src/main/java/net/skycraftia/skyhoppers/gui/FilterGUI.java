package net.skycraftia.skyhoppers.gui;

import net.skycraftia.skyhoppers.SkyHoppersPlugin;
import net.skycraftia.skyhoppers.manager.HopperManager;
import net.skycraftia.skyhoppers.obj.SkyHopper;
import net.skycraftia.skyhoppers.obj.FilterType;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import xyz.oribuin.gui.Item;
import xyz.oribuin.gui.PaginatedGui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

import static xyz.oribuin.orilibrary.util.HexUtils.colorify;

public class FilterGUI {

    private final SkyHoppersPlugin plugin;
    private final SkyHopper hopper;
    private final HopperManager hopperManager;

    private final List<FilterType> filterTypes;
    private ListIterator<FilterType> iterator;
    private FilterType currentType;

    public FilterGUI(final SkyHoppersPlugin plugin, SkyHopper hopper) {
        this.plugin = plugin;
        this.hopper = hopper;
        this.hopperManager = plugin.getManager(HopperManager.class);

        // List inception
        this.filterTypes = new ArrayList<>(Arrays.asList(FilterType.values()));
        filterTypes.removeIf(filterType -> filterType == hopper.getFilterType());
        filterTypes.add(0, hopper.getFilterType());

        this.iterator = new ArrayList<>(filterTypes).listIterator();
        this.currentType = iterator.next();
    }

    public void createGui(Player player) {

        // Define all the page slots.
        final List<Integer> pageSlots = new ArrayList<>();
        for (int i = 9; i < 36; i++)
            pageSlots.add(i);

        final PaginatedGui gui = new PaginatedGui(45, "Hopper Filter: " + StringUtils.capitalize(hopper.getFilterType().name().toLowerCase()), pageSlots);
        gui.setDefaultClickFunction(event -> {

            // Stop the ability to yoink items.
            event.setResult(Event.Result.DENY);
            event.setCancelled(true);
            ((Player) event.getWhoClicked()).updateInventory();

            // Don't remove any item that isnt in the page gui
            if (!pageSlots.contains(event.getSlot())) {
                return;
            }

            // Check if they clicked ona valid item.
            final ItemStack item = event.getCurrentItem();
            if (item == null || item.getType() == Material.AIR)
                return;

            // Remove the item from the hopper's current filtered items.
            // Don't update gui if nothing was removed.
            if (this.hopper.getFilterItems().remove(item.getType())) {
                // Update the gui with the new items
                this.addFilterBlocks(gui);
                gui.update();
            }
        });

        gui.setPersonalClickAction(event -> {

            // Run default function for the player's inventory click event.
            gui.getDefaultClickFunction().accept(event);

            // Get the item they clicked and make sure it's a valid item
            final ItemStack currentItem = event.getCurrentItem();
            if (currentItem == null || currentItem.getType() == Material.AIR)
                return;

            // Don't try to add any items that are already in the filtered items list.
            List<Material> filterItems = hopper.getFilterItems();
            if (filterItems.contains(currentItem.getType()))
                return;

            hopper.getFilterItems().add(currentItem.getType());
            // Update the gui with the new items
            this.addFilterBlocks(gui);
            gui.update();
        });

        gui.setCloseAction(event -> this.hopperManager.saveHopper(hopper));

        gui.setItems(Arrays.asList(0, 8, 36, 44), Item.filler(Material.CYAN_STAINED_GLASS_PANE));
        gui.setItems(Arrays.asList(1, 2, 6, 7, 37, 38, 42, 43), Item.filler(Material.LIGHT_BLUE_STAINED_GLASS_PANE));
        gui.setItems(Arrays.asList(3, 4, 5, 39, 40, 41), Item.filler(Material.GRAY_STAINED_GLASS_PANE));
        gui.setItem(40, new Item.Builder(Material.BARRIER)
                .setName(colorify("#FF4F58&lGo Back!"))
                .setLore(colorify("&7Click to go back to"))
                .create(), event -> new HopperGUI(this.plugin).create(hopper, (Player) event.getWhoClicked()));

        gui.setItem(38, new Item.Builder(Material.PAPER)
                .setName(colorify("#FF4F58&lBack Page"))
                .setLore(colorify("&7Click to go to"), colorify("the previous page!"))
                .create(), event -> gui.previous(event.getWhoClicked()));

        gui.setItem(42, new Item.Builder(Material.PAPER)
                .setName(colorify("#FF4F58&lNext Page"))
                .setLore(colorify("&7Click to go to"), colorify("the next page!"))
                .create(), event -> gui.next(event.getWhoClicked()));

        this.addHopperItem(gui);
        this.addFilterBlocks(gui);

        gui.open(player);
    }

    /**
     * Add the item to switch the current hopper filter.
     *
     * @param gui The gui the item is being added to.
     */
    private void addHopperItem(PaginatedGui gui) {
        gui.setItem(4, new Item.Builder(Material.HOPPER)
                .setName(colorify("#99ff99&lSwitch Filter &7| &f" + StringUtils.capitalize(hopper.getFilterType().name().toLowerCase())))
                .create(), event -> {

            if (!iterator.hasNext()) {
                iterator = new ArrayList<>(this.filterTypes).listIterator();
            }

            currentType = iterator.next();
            this.hopper.setFilterType(currentType);
            this.addHopperItem(gui);
            gui.update();
            gui.updateTitle("Hopper Filter: " + StringUtils.capitalize(hopper.getFilterType().name().toLowerCase()));
        });
    }

    /**
     * Add all the filtered items from the gui into the menu
     *
     * @param gui The gui the filter blocks are being added to.
     */
    private void addFilterBlocks(PaginatedGui gui) {

        // Remove page items so there's no item duplication.
        gui.getPageItems().clear();
        // Add back all the filtered items into the gui
        this.hopper.getFilterItems().forEach(material -> gui.addPageItem(new ItemStack(material), event -> this.hopper.getFilterItems().remove(material)));

    }

}
