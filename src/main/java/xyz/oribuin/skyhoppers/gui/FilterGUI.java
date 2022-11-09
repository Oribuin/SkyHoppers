package xyz.oribuin.skyhoppers.gui;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import xyz.oribuin.skyhoppers.SkyHoppersPlugin;
import xyz.oribuin.skyhoppers.manager.HopperManager;
import xyz.oribuin.skyhoppers.manager.LocaleManager;
import xyz.oribuin.skyhoppers.obj.FilterType;
import xyz.oribuin.skyhoppers.obj.SkyHopper;
import xyz.oribuin.skyhoppers.util.ItemMaker;
import xyz.oribuin.skyhoppers.util.PluginUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

public class FilterGUI {

    private final SkyHoppersPlugin plugin;
    private final HopperManager manager;
    private final LocaleManager locale;
    private final SkyHopper skyHopper;

    private final List<FilterType> filters;
    private ListIterator<FilterType> iterator;
    private FilterType current;

    public FilterGUI(final SkyHoppersPlugin plugin, SkyHopper skyHopper) {
        this.plugin = plugin;
        this.manager = this.plugin.getManager(HopperManager.class);
        this.locale = this.plugin.getManager(LocaleManager.class);
        this.skyHopper = skyHopper;

        this.filters = new ArrayList<>(Arrays.asList(FilterType.values()));
        filters.removeIf(filterType -> filterType == skyHopper.getFilterType());
        filters.add(0, skyHopper.getFilterType());

        this.iterator = new ArrayList<>(filters).listIterator();
        this.current = iterator.next();
    }

    public void openGUI(Player player) {
        PaginatedGui gui = Gui.paginated()
                .title(this.text("Hopper Filters"))
                .disableAllInteractions()
                .rows(5)
                .create();

        gui.setCloseGuiAction(event -> this.manager.saveHopperBlock(skyHopper));
        gui.setPlayerInventoryAction(event -> {
            List<Material> materials = new ArrayList<>(this.skyHopper.getFilterItems());

            if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) {
                return;
            }

            // disableAllInteractions doesn't do anything here, so we have to do it manually.
            event.setCancelled(true);
            event.setResult(Event.Result.DENY);
            ((Player) event.getWhoClicked()).updateInventory();


            materials.add(event.getCurrentItem().getType());
            this.skyHopper.setFilterItems(materials);
            this.addFilterItems(gui);
        });

        gui.setItem(List.of(0, 8, 36, 44), new GuiItem(ItemMaker.filler(Material.CYAN_STAINED_GLASS_PANE)));
        gui.setItem(List.of(1, 2, 6, 7, 9, 17, 27, 35, 37, 38, 42, 43), new GuiItem(ItemMaker.filler(Material.LIGHT_BLUE_STAINED_GLASS_PANE)));
        gui.setItem(List.of(3, 5, 18, 26, 39, 41), new GuiItem(ItemMaker.filler(Material.BLUE_STAINED_GLASS_PANE)));
        gui.setItem(40,
                ItemBuilder.from(Material.BARRIER)
                        .name(this.text("<#FF4F58><bold>Go Back!"))
                        .lore(this.text(" <white>| <gray>Click to go back to the"), this.text(" <white>| <gray>main hopper menu."))
                        .asGuiItem(e -> new HopperGUI(this.plugin).openGUI(player, this.skyHopper))
        );

        this.setFilterItem(gui);
        this.addFilterItems(gui);

        gui.open(player);

    }

    public void addFilterItems(PaginatedGui gui) {
        List<Material> materials = new ArrayList<>(this.skyHopper.getFilterItems());

        gui.clearPageItems();
        materials.forEach(material -> gui.addItem(
                ItemBuilder.from(material)
                        .name(this.text("<#FF4F58><bold>" + material.name()))
                        .lore(this.text(" <white>| <gray>Click to remove this item from the filter."))
                        .asGuiItem(e -> {
                            this.skyHopper.getFilterItems().remove(material);
                            this.manager.saveHopperBlock(this.skyHopper);
                            this.openGUI((Player) e.getWhoClicked());
                        })
        ));

        gui.update();
    }

    public void setFilterItem(PaginatedGui gui) {
        gui.setItem(4, ItemBuilder.from(Material.HOPPER)
                .name(this.text("<#99ff99><bold>Change Item Filter Type"))
                .lore(
                        this.text(" <white>| <gray>Click to switch the filter type."),
                        this.text(" <white>| <gray>Current: <white>" + PluginUtils.formatEnum(this.skyHopper.getFilterType().name())),
                        this.text(" <white>| <gray>Next: <white>" + PluginUtils.formatEnum(this.getNextFilter().name())),
                        this.text(" <white>|"),
                        this.text(" <white>| <gray>" + this.skyHopper.getFilterType().getDesc())
                )
                .glow(true)
                .asGuiItem(event -> {
                    if (!iterator.hasNext()) {
                        iterator = new ArrayList<>(this.filters).listIterator();
                    }

                    current = iterator.next();
                    this.skyHopper.setFilterType(current);
                    this.setFilterItem(gui);
                }));

        gui.update();
    }

    public FilterType getNextFilter() {
        // Get the next filter type from the list from the current filter type.
        int index = this.filters.indexOf(this.skyHopper.getFilterType()) + 1;

        // If the index is greater than the size of the list, reset the index to 0.
        if (index >= this.filters.size()) {
            index = 0;
        }

        // Return the next filter type.
        return this.filters.get(index);
    }

    private Component text(String text) {
        return MiniMessage.miniMessage().deserialize(text)
                .decoration(TextDecoration.ITALIC, false)
                .asComponent();
    }

}
