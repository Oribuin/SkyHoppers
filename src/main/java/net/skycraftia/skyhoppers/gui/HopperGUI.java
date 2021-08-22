package net.skycraftia.skyhoppers.gui;

import net.skycraftia.skyhoppers.SkyHoppersPlugin;
import net.skycraftia.skyhoppers.manager.HopperManager;
import net.skycraftia.skyhoppers.manager.MessageManager;
import net.skycraftia.skyhoppers.obj.SkyHopper;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import xyz.oribuin.gui.Gui;
import xyz.oribuin.gui.Item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static xyz.oribuin.orilibrary.util.HexUtils.colorify;

public class HopperGUI {

    private final SkyHoppersPlugin plugin;
    private final HopperManager hopperManager;

    public HopperGUI(final SkyHoppersPlugin plugin) {
        this.plugin = plugin;
        this.hopperManager = this.plugin.getManager(HopperManager.class);
    }

    public void create(SkyHopper hp, Player player) {
        final Gui gui = new Gui(45, "Sky Hopper");
        gui.setDefaultClickFunction(event -> {
            event.setResult(Event.Result.DENY);
            event.setCancelled(true);
            ((Player) event.getWhoClicked()).updateInventory();
        });

        final List<Integer> graySlots = new ArrayList<>();
        for (int i = 0; i < 45; i++)
            graySlots.add(i);

        gui.setItems(graySlots, Item.filler(Material.GRAY_STAINED_GLASS_PANE));

        gui.setItems(Arrays.asList(0, 8, 36, 44), Item.filler(Material.CYAN_STAINED_GLASS_PANE));
        gui.setItems(Arrays.asList(1, 2, 6, 7, 9, 17, 27, 35, 37, 38, 42, 43), Item.filler(Material.LIGHT_BLUE_STAINED_GLASS_PANE));
        gui.setItems(Arrays.asList(3, 4, 5, 18, 26, 39, 40, 41), Item.filler(Material.WHITE_STAINED_GLASS_PANE));

        if (player.hasPermission("skyhoppers.view")) {
            gui.setItem(4, new Item.Builder(Material.SPYGLASS)
                    .setName(colorify("#99ff99&lVisualise Hopper"))
                    .setLore(colorify("&7Click to view this hopper,"), colorify("&7the hopper's current chunk and"), colorify("&7the linked chest in particle form!"))
                    .create(), event -> {
                event.getWhoClicked().closeInventory();
                Bukkit.dispatchCommand(event.getWhoClicked(), "hoppers view");
            });
        }

        this.setItems(gui, hp);

        gui.open(player);
    }

    private void setItems(Gui gui, SkyHopper hp) {
        gui.setItem(20, new Item.Builder(hp.isEnabled() ? Material.LIME_DYE : Material.RED_DYE)
                .setName(colorify("#99ff99&lChunk Suction"))
                .setLore(colorify("&7Click to toggle chunk suction."),
                        " ",
                        colorify("&7Suction is " + (hp.isEnabled() ? "#87E878Enabled" : "#FF4F58Disabled"))
                )
                .create(), event -> {
            hp.setEnabled(!hp.isEnabled());
            hopperManager.saveHopper(hp);
            this.setItems(gui, hp);
            gui.update();
        });

        gui.setItem(22, new Item.Builder(Material.CHEST)
                .setName(colorify("#99ff99&lLink Container"))
                .setLore(colorify("&7Click to change the current"),
                        colorify("&7linked container"),
                        " ",
                        colorify("#FF4F58Destroy &7your linked container"),
                        colorify("&7container to unlink it.")
                )
                .create(), event -> {
            event.getWhoClicked().closeInventory();
            this.plugin.getManager(MessageManager.class).send(event.getWhoClicked(), "link-container");
            this.plugin.getLinkingPlayers().put(event.getWhoClicked().getUniqueId(), hp);
        });

        gui.setItem(24, new Item.Builder(Material.HOPPER)
                .setName(colorify("#99ff99&lManage Filter"))
                .setLore(colorify("&7Whitelist or Blacklist any item"),
                        colorify("&7from this hopper."),
                        colorify(" "),
                        colorify("#99ff99&lFilter &7: &f" + StringUtils.capitalize(hp.getFilterType().name().toLowerCase()))
                )
                .create(), event -> new FilterGUI(plugin, hp).createGui((Player) event.getWhoClicked()));
    }

}
