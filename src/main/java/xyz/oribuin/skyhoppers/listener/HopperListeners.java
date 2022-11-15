package xyz.oribuin.skyhoppers.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import xyz.oribuin.skyhoppers.SkyHoppersPlugin;
import xyz.oribuin.skyhoppers.manager.HopperManager;
import xyz.oribuin.skyhoppers.util.PluginUtils;

public class HopperListeners implements Listener {

    private final HopperManager hopperManager;

    public HopperListeners(final SkyHoppersPlugin plugin) {
        this.hopperManager = plugin.getManager(HopperManager.class);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onHopperPickup(InventoryPickupItemEvent event) {

        if (!(event.getInventory().getHolder() instanceof org.bukkit.block.Hopper block))
            return;

        var hopper = this.hopperManager.getHopper(block);
        if (hopper == null)
            return;

        if (!hopper.isEnabled()) {
            event.setCancelled(true);
            return;
        }

        final var item = event.getItem().getItemStack();
        if (PluginUtils.itemFiltered(item, hopper)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onHopperMoveItem(InventoryMoveItemEvent event) {
        if (!(event.getDestination().getHolder() instanceof org.bukkit.block.Hopper destination))
            return;

        final var hopper = this.hopperManager.getHopper(destination);
        if (hopper == null)
            return;

        final var item = event.getItem();
        if (PluginUtils.itemFiltered(item, hopper)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onHopperItemAdd(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof org.bukkit.block.Hopper block))
            return;

        final var hopper = this.hopperManager.getHopper(block);
        if (hopper == null)
            return;

        final var item = event.getCurrentItem();
        if (item == null)
            return;

        if (PluginUtils.itemFiltered(item, hopper)) {
            event.setCancelled(true);
        }

    }


}
