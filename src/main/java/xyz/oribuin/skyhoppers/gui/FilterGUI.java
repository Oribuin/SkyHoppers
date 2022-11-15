package xyz.oribuin.skyhoppers.gui;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import xyz.oribuin.skyhoppers.hopper.SkyHopper;
import xyz.oribuin.skyhoppers.hopper.filter.FilterOption;
import xyz.oribuin.skyhoppers.hopper.filter.FilterType;
import xyz.oribuin.skyhoppers.manager.HopperManager;
import xyz.oribuin.skyhoppers.manager.MenuManager;
import xyz.oribuin.skyhoppers.util.PluginUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class FilterGUI extends PluginMenu {

    private final HopperManager manager;
    private final Map<UUID, FilterOption> optionMap = new HashMap<>();

    public FilterGUI(final RosePlugin rosePlugin) {
        super(rosePlugin);

        this.manager = rosePlugin.getManager(HopperManager.class);
    }

    public void openGUI(Player player, SkyHopper hopper) {
        var gui = Gui.paginated()
                .title(this.text(this.config.getString("gui-settings.title")))
                .rows(this.config.getInt("gui-settings.rows"))
                .disableAllInteractions()
                .create();

        gui.setCloseGuiAction(event -> this.manager.saveHopperBlock(hopper));
        gui.setPlayerInventoryAction(event -> {
            var materials = new ArrayList<>(hopper.getFilterItems());

            if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) {
                return;
            }

            // disableAllInteractions doesn't do anything here, so we have to do it manually.
            event.setCancelled(true);
            event.setResult(Event.Result.DENY);
            ((Player) event.getWhoClicked()).updateInventory();

            if (!materials.contains(event.getCurrentItem().getType())) {
                materials.add(event.getCurrentItem().getType());
            } else {
                materials.remove(event.getCurrentItem().getType());
            }

            hopper.setFilterItems(materials);
            this.addFilterItems(gui, hopper);
        });

        final var extraItems = this.config.getConfigurationSection("extra-items");
        if (extraItems != null) {
            for (var key : extraItems.getKeys(false)) {
                MenuItem.create(this.config)
                        .path("extra-items." + key)
                        .player(player)
                        .place(gui);
            }
        }

        MenuItem.create(this.config)
                .path("go-back")
                .player(player)
                .action(event -> this.rosePlugin.getManager(MenuManager.class).getGUI(HopperGUI.class).openGUI(player, hopper))
                .place(gui);

        MenuItem.create(this.config)
                .path("next-page")
                .player(player)
                .action(event -> gui.next())
                .conditional(gui.getNextPageNum() > gui.getCurrentPageNum())
                .place(gui);

        MenuItem.create(this.config)
                .path("previous-page")
                .player(player)
                .action(event -> gui.previous())
                .conditional(gui.getPrevPageNum() < gui.getCurrentPageNum())
                .place(gui);

        this.setFilterItem(gui, player, hopper);
        this.addFilterItems(gui, hopper);
        gui.open(player);

    }

    public void addFilterItems(PaginatedGui gui, SkyHopper hopper) {
        var materials = new ArrayList<>(hopper.getFilterItems());

        gui.clearPageItems();
        materials.forEach(material -> gui.addItem(new GuiItem(material, e -> {
            hopper.getFilterItems().remove(material);
            this.manager.saveHopperBlock(hopper);
            this.openGUI((Player) e.getWhoClicked(), hopper);
        })));

        gui.update();
    }

    public void setFilterItem(PaginatedGui gui, Player player, SkyHopper hopper) {

        var option = this.optionMap.getOrDefault(player.getUniqueId(), new FilterOption(hopper.getFilterType()));

        if (!option.getIterator().hasNext()) {
            option.setIterator(Arrays.asList(FilterType.values()).iterator());
        }

        var next = option.getIterator().next();

        final var placeholders = StringPlaceholders.builder()
                .addPlaceholder("current", PluginUtils.formatEnum(option.getFilterType().name()))
                .addPlaceholder("next", PluginUtils.formatEnum(next.name()))
                .addPlaceholder("description", hopper.getFilterType().getDesc())
                .build();

        MenuItem.create(this.config)
                .path("change-filter")
                .placeholders(placeholders)
                .player(player)
                .action(event -> {
                    option.setFilterType(next);
                    this.optionMap.put(player.getUniqueId(), option);
                    hopper.setFilterType(next);
                    this.setFilterItem(gui, player, hopper);
                })
                .place(gui);
    }

    private Component text(String text) {
        if (text == null) {
            return Component.empty();
        }

        return MiniMessage.miniMessage().deserialize(text)
                .decoration(TextDecoration.ITALIC, false)
                .asComponent();
    }


    @Override
    public Map<String, Object> getDefaultValues() {
        return new LinkedHashMap<>() {{
            this.put("#0", "GUI Settings");
            this.put("gui-settings.title", "Hopper Filters");
            this.put("gui-settings.rows", 5);

            this.put("#1", "Change Filter");
            this.put("change-filter.material", "HOPPER");
            this.put("change-filter.name", "#99FF99&lChange Item Filter Type");
            this.put("change-filter.slot", 4);
            this.put("change-filter.lore", List.of(
                    " &f| &7Click to switch the filter type.",
                    " &f| &7Current: &f%current%",
                    " &f| &7Next: &f%next%",
                    " &f|",
                    " &f| &7%description%"
            ));


            this.put("#2", "Go Back");
            this.put("go-back.material", "BARRIER");
            this.put("go-back.name", "#FF4F58&lGo Back");
            this.put("go-back.lore", List.of(
                    " &f| &7Click to go back to the",
                    " &f| &7hopper menu."
            ));
            this.put("go-back.slot", 40);

            this.put("#3", "Next Page");
            this.put("next-page.material", "PAPER");
            this.put("next-page.name", "#99FF99&lNext Page");
            this.put("next-page.lore", List.of(
                    " &f| &7Click to go to",
                    " &f| &7the next page."
            ));
            this.put("next-page.slot", 5);

            this.put("#4", "Previous Page");
            this.put("previous-page.material", "PAPER");
            this.put("previous-page.name", "#99FF99&lPrevious Page");
            this.put("previous-page.lore", List.of(
                    " &f| &7Click to go to",
                    " &f| &7the previous page."
            ));
            this.put("previous-page.slot", 3);

            this.put("#5", "Extra Items");
            this.put("extra-items.0.material", "CYAN_STAINED_GLASS_PANE");
            this.put("extra-items.0.slots", List.of(0, 8, 36, 44));
            this.put("extra-items.0.name", " ");

            this.put("extra-items.1.material", "LIGHT_BLUE_STAINED_GLASS_PANE");
            this.put("extra-items.1.slots", List.of(1, 2, 6, 7, 9, 17, 27, 35, 37, 38, 42, 43));
            this.put("extra-items.1.name", " ");

            this.put("extra-items.2.material", "BLUE_STAINED_GLASS_PANE");
            this.put("extra-items.2.slots", List.of(3, 5, 18, 26, 39, 41));
            this.put("extra-items.2.name", " ");

        }};
    }

    @Override
    public String getMenuName() {
        return "filter-gui";
    }

}
