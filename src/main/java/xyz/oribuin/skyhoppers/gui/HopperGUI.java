package xyz.oribuin.skyhoppers.gui;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import xyz.oribuin.skyhoppers.SkyHoppersPlugin;
import xyz.oribuin.skyhoppers.manager.HopperManager;
import xyz.oribuin.skyhoppers.manager.LocaleManager;
import xyz.oribuin.skyhoppers.obj.SkyHopper;
import xyz.oribuin.skyhoppers.util.ItemMaker;
import xyz.oribuin.skyhoppers.util.PluginUtils;

import java.util.ArrayList;
import java.util.List;

public class HopperGUI {

    private final SkyHoppersPlugin plugin;
    private final LocaleManager locale;
    private final HopperManager manager;

    public HopperGUI(SkyHoppersPlugin plugin) {
        this.plugin = plugin;
        this.locale = this.plugin.getManager(LocaleManager.class);
        this.manager = this.plugin.getManager(HopperManager.class);

    }

    public void openGUI(Player player, SkyHopper skyHopper) {
        final var hoppers = manager.getHopperViewers();

        Gui gui = Gui.gui()
                .title(this.text("SkyHoppers GUI"))
                .disableAllInteractions()
                .rows(5)
                .create();

        List<Integer> graySlots = new ArrayList<>();
        for (int i = 0; i < 45; i++) {
            graySlots.add(i);
        }

        gui.setItem(graySlots, new GuiItem(ItemMaker.filler(Material.GRAY_STAINED_GLASS_PANE)));
        gui.setItem(List.of(0, 8, 36, 44), new GuiItem(ItemMaker.filler(Material.CYAN_STAINED_GLASS_PANE)));
        gui.setItem(List.of(1, 2, 6, 7, 9, 17, 27, 35, 37, 38, 42, 43), new GuiItem(ItemMaker.filler(Material.LIGHT_BLUE_STAINED_GLASS_PANE)));
        gui.setItem(List.of(3, 4, 5, 18, 26, 39, 40, 41), new GuiItem(ItemMaker.filler(Material.WHITE_STAINED_GLASS_PANE)));

        gui.setItem(4, ItemBuilder.from(Material.SPYGLASS)
                .name(this.text("<#99ff99><bold>Visualize Hopper"))
                .lore(
                        this.text(" <white>| <gray>Click to view the radius"),
                        this.text(" <white>| <gray>of the hopper suction and"),
                        this.text(" <white>| <gray>any linked containers."))
                .asGuiItem(event -> {

                    if (hoppers.containsKey(player.getUniqueId())) {
                        hoppers.remove(player.getUniqueId());
                        locale.sendMessage(player, "command-view-disabled");
                        return;
                    }

                    hoppers.put(player.getUniqueId(), skyHopper);
                    locale.sendMessage(player, "command-view-success");
                    gui.close(player);
                })
        );

        this.setItems(gui, skyHopper);
        gui.open(player);
    }

    private Component text(String text) {
        return MiniMessage.miniMessage().deserialize(text)
                .decoration(TextDecoration.ITALIC, false)
                .asComponent();
    }

    private void setItems(Gui gui, SkyHopper hopper) {
        gui.setItem(20, ItemBuilder.from(hopper.isEnabled() ? Material.LIME_DYE : Material.RED_DYE)
                .name(this.text("<#99ff99><bold>Hopper Suction"))
                .lore(
                        this.text(" <white>| <gray>Click to toggle the hopper suction."),
                        this.text(""),
                        this.text(" <white>| <gray> Suction is " + (hopper.isEnabled() ? "<#87E878>Enabled" : "<#FF4F58>Disabled"))
                )
                .asGuiItem(event -> {
                    hopper.setEnabled(!hopper.isEnabled());
                    this.manager.saveHopperBlock(hopper);
                    this.setItems(gui, hopper);
                }));

        gui.setItem(22, ItemBuilder.from(Material.CHEST)
                .name(this.text("<#99ff99><bold>Linked Containers"))
                .lore(
                        this.text(" <white>| <gray>Click to change the current"),
                        this.text(" <white>| <gray>linked containers."),
                        this.text(""),
                        this.text(" <white>| <#FF4F58>Destroy <gray>your linked container"),
                        this.text(" <white>| <gray>container to unlink it."))
                .asGuiItem(event -> {
                    gui.close(event.getWhoClicked());
                    this.locale.sendMessage(event.getWhoClicked(), "hopper-linked-success");
                    this.plugin.getLinkingPlayers().put(event.getWhoClicked().getUniqueId(), hopper);
                })
        );

        // Manage filter
        gui.setItem(24, ItemBuilder.from(Material.HOPPER)
                .name(this.text("<#99ff99><bold>Manage Filter"))
                .lore(
                        this.text(" <white>| <gray>Whitelist or Blacklist any"),
                        this.text(" <white>| <gray>item from this hopper."),
                        this.text(""),
                        this.text(" <white>| <#99ff99>Filter <gray>| <white>" + PluginUtils.formatEnum(hopper.getFilterType().name()))
                )
                .asGuiItem(event -> new FilterGUI(this.plugin, hopper).openGUI((Player) event.getWhoClicked()))
        );

        gui.update();
    }

}
