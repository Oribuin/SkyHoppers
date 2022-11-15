package xyz.oribuin.skyhoppers.gui;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import dev.triumphteam.gui.guis.Gui;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import xyz.oribuin.skyhoppers.SkyHoppersPlugin;
import xyz.oribuin.skyhoppers.hopper.SkyHopper;
import xyz.oribuin.skyhoppers.manager.HopperManager;
import xyz.oribuin.skyhoppers.manager.LocaleManager;
import xyz.oribuin.skyhoppers.manager.MenuManager;
import xyz.oribuin.skyhoppers.util.PluginUtils;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HopperGUI extends PluginMenu {

    private final SkyHoppersPlugin plugin;
    private final LocaleManager locale;
    private final HopperManager manager;

    public HopperGUI(RosePlugin plugin) {
        super(plugin);

        this.plugin = (SkyHoppersPlugin) plugin;
        this.locale = this.plugin.getManager(LocaleManager.class);
        this.manager = this.plugin.getManager(HopperManager.class);
    }


    public void openGUI(Player player, SkyHopper skyHopper) {
        final var hoppers = manager.getHopperViewers();

        var gui = this.createGUI(player);

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
                .path("visualiser")
                .player(player)
                .action(event -> {
                    if (hoppers.containsKey(player.getUniqueId())) {
                        hoppers.remove(player.getUniqueId());
                        locale.sendMessage(player, "command-view-disabled");
                        return;
                    }

                    hoppers.put(player.getUniqueId(), skyHopper);
                    locale.sendMessage(player, "command-view-success");
                    gui.close(player);
                })
                .place(gui);

        this.setItems(gui, skyHopper, player);
        gui.open(player);
    }

    private void setItems(Gui gui, SkyHopper hopper, Player player) {

        final var suctionEnabled = this.config.getString("suction.enabled-message");
        final var suctionDisabled = this.config.getString("suction.disabled-message");

        final var disabledMaterialName = this.config.getString("suction.disabled-material");
        var material = Material.RED_DYE;
        if (disabledMaterialName != null) {
            material = Material.getMaterial(disabledMaterialName);

            if (material == null) {
                material = Material.RED_DYE;
            }

        }

        var suctionItem = MenuItem.create(this.config)
                .path("suction")
                .placeholders(StringPlaceholders.single("status", hopper.isEnabled() ? suctionEnabled : suctionDisabled))
                .player(player)
                .action(event -> {
                    hopper.setEnabled(!hopper.isEnabled());
                    this.manager.saveHopperBlock(hopper);
                    this.setItems(gui, hopper, player);
                });

        if (!hopper.isEnabled()) {
            suctionItem.customItem(material);
        }

        suctionItem.place(gui);

        MenuItem.create(this.config)
                .path("linked")
                .player(player)
                .action(event -> {
                    gui.close(event.getWhoClicked());
                    this.locale.sendMessage(event.getWhoClicked(), "hopper-linked-success");
                    this.plugin.getLinkingPlayers().put(event.getWhoClicked().getUniqueId(), hopper);
                })
                .place(gui);

        MenuItem.create(this.config)
                .path("filter")
                .placeholders(StringPlaceholders.single("filter", PluginUtils.formatEnum(hopper.getFilterType().name())))
                .player(player)
                .action(event -> this.plugin.getManager(MenuManager.class)
                        .getGUI(FilterGUI.class)
                        .openGUI((Player) event.getWhoClicked(), hopper))
                .place(gui);

        gui.update();
    }

    @Override
    public Map<String, Object> getDefaultValues() {
        return new LinkedHashMap<>() {{
            this.put("#0", "GUI Settings");
            this.put("#1", "title - The title of the GUI");
            this.put("#2", "rows - The amount of rows in the GUI");
            this.put("gui-settings.title", "Sky Hopper GUI");
            this.put("gui-settings.rows", 5);

            this.put("#3", "Hopper Visualiser");
            this.put("visualiser.enabled", true);
            this.put("visualiser.material", "SPYGLASS");
            this.put("visualiser.name", "#99ff99&lVisualize Hopper");
            this.put("visualiser.lore", Arrays.asList(
                    " &f| &7Click to view the radius",
                    " &f| &7of the hopper suction and",
                    " &f| &7any linked containers."
            ));
            this.put("visualiser.slot", 4);

            this.put("#4", "Hopper Suction");
            this.put("suction.enabled", true);
            this.put("suction.material", "LIME_DYE");
            this.put("suction.name", "#99ff99&lHopper Suction");
            this.put("suction.lore", Arrays.asList(
                    " &f| &7Click to toggle the hopper suction.",
                    " &f| ",
                    " &f| &7Suction is #99ff99%status%"
            ));
            this.put("suction.slot", 20);
            this.put("suction.enabled-message", "#87E878Enabled");
            this.put("suction.disabled-message", "#FF4F58Disabled");
            this.put("suction.disabled-material", "RED_DYE");

            this.put("#5", "Linked Containers");
            this.put("linked.enabled", true);
            this.put("linked.material", "CHEST");
            this.put("linked.name", "#99ff99&lLinked Containers");
            this.put("linked.lore", Arrays.asList(
                    " &f| &7Click to change the current",
                    " &f| &7linked containers.",
                    " &f| ",
                    " &f| #FF4F58Destroy&7 your linked",
                    " &f| &7container to unlink it."
            ));
            this.put("linked.slot", 22);

            this.put("#6", "Manage Filter");
            this.put("filter.enabled", true);
            this.put("filter.material", "HOPPER");
            this.put("filter.name", "#99ff99&lManage Filter");
            this.put("filter.lore", Arrays.asList(
                    " &f| &7Whitelist or Blacklist any",
                    " &f| &7item from this hopper.",
                    " &f| ",
                    " &f| #99ff99Filter&f | &f%filter%"
            ));
            this.put("filter.slot", 24);

            this.put("#7", "Extra GUI Display Items");

            this.put("extra-items.1.material", "CYAN_STAINED_GLASS_PANE");
            this.put("extra-items.1.name", " ");
            this.put("extra-items.1.slots", List.of(0, 8, 36, 44));

            this.put("extra-items.2.material", "LIGHT_BLUE_STAINED_GLASS_PANE");
            this.put("extra-items.2.name", " ");
            this.put("extra-items.2.slots", List.of(1, 2, 6, 7, 9, 17, 27, 35, 37, 38, 42, 43));

            this.put("extra-items.3.material", "WHITE_STAINED_GLASS_PANE");
            this.put("extra-items.3.name", " ");
            this.put("extra-items.3.slots", List.of(3, 4, 5, 18, 26, 39, 40, 41));


        }};
    }

    @Override
    public String getMenuName() {
        return "hopper-gui";
    }

}
