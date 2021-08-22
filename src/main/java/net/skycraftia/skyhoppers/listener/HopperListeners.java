package net.skycraftia.skyhoppers.listener;

import net.skycraftia.skyhoppers.SkyHoppersPlugin;
import net.skycraftia.skyhoppers.manager.HopperManager;
import net.skycraftia.skyhoppers.obj.SkyHopper;
import net.skycraftia.skyhoppers.obj.FilterType;
import net.skycraftia.skyhoppers.util.PluginUtils;
import org.bukkit.block.Hopper;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class HopperListeners implements Listener {

    private final HopperManager hopperManager;

    public HopperListeners(final SkyHoppersPlugin plugin) {
        this.hopperManager = plugin.getManager(HopperManager.class);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onHopperPickup(InventoryPickupItemEvent event) {

        if (!(event.getInventory().getHolder() instanceof Hopper block))
            return;

        final Optional<SkyHopper> customHopper = this.hopperManager.getHopperFromBlock(block);
        if (customHopper.isEmpty())
            return;

        final ItemStack item = event.getItem().getItemStack();
        if (PluginUtils.itemFiltered(item, customHopper.get())) {
            if (customHopper.get().getFilterType() == FilterType.DESTROY)
                event.getItem().remove();

            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onHopperMoveItem(InventoryMoveItemEvent event) {
        if (!(event.getDestination().getHolder() instanceof Hopper destination))
            return;

        final Optional<SkyHopper> optional = this.hopperManager.getHopperFromBlock(destination);
        if (optional.isEmpty())
            return;

        final ItemStack item = event.getItem();
        if (PluginUtils.itemFiltered(item, optional.get())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onHopperItemAdd(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof Hopper block))
            return;

        final Optional<SkyHopper> customHopper = this.hopperManager.getHopperFromBlock(block);
        if (customHopper.isEmpty())
            return;

        final ItemStack item = event.getCurrentItem();
        if (item == null)
            return;

        if (PluginUtils.itemFiltered(item, customHopper.get())) {
            event.setCancelled(true);
        }

    }


}
