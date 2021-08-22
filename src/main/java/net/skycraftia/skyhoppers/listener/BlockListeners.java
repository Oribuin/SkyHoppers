package net.skycraftia.skyhoppers.listener;

import net.skycraftia.skyhoppers.SkyHoppers;
import net.skycraftia.skyhoppers.manager.DataManager;
import net.skycraftia.skyhoppers.manager.HopperManager;
import net.skycraftia.skyhoppers.manager.MessageManager;
import net.skycraftia.skyhoppers.obj.CustomHopper;
import org.bukkit.Material;
import org.bukkit.block.Hopper;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.*;

import static net.skycraftia.skyhoppers.util.PluginUtils.getBlockLoc;

/**
 * @author Oribuin
 */
public class BlockListeners implements Listener {

    private final SkyHoppers plugin;
    private final DataManager data;
    private final MessageManager msg;
    private final HopperManager hopperManager;

    public BlockListeners(final SkyHoppers plugin) {
        this.plugin = plugin;
        this.data = plugin.getManager(DataManager.class);
        this.msg = plugin.getManager(MessageManager.class);
        this.hopperManager = plugin.getManager(HopperManager.class);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onHopperPlace(BlockPlaceEvent event) {

        if (!(event.getBlock().getState() instanceof Hopper hopper)) {
            return;
        }

        final ItemStack itemInHand = event.getItemInHand();
        final Optional<CustomHopper> handHopper = this.hopperManager.getHopperFromItem(itemInHand);
        if (handHopper.isEmpty())
            return;

        if (this.hopperManager.getHopperFromBlock(hopper).isPresent())
            return;

        handHopper.get().setLocation(getBlockLoc(event.getBlock().getLocation()));
        this.data.saveHopper(handHopper.get());
        this.hopperManager.saveHopper(handHopper.get());
        this.msg.send(event.getPlayer(), "placed-hopper");
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onHopperBreak(BlockBreakEvent event) {

        if (!(event.getBlock().getState() instanceof Hopper hopper)) {
            return;
        }

        final Optional<CustomHopper> customHopper = this.hopperManager.getHopperFromBlock(hopper);
        if (customHopper.isEmpty())
            return;

        // Remove the hopper from the visualizer.
        Map<UUID, CustomHopper> hopperViewers = this.plugin.getHopperViewTask().getHopperViewers();
        hopperViewers.entrySet().removeIf(entry -> entry.getValue().getLocation() != null
                && getBlockLoc(entry.getValue().getLocation()).equals(getBlockLoc(hopper.getLocation())));

        // Delete the hopper's data
        this.data.deleteHopper(customHopper.get());
        this.msg.send(event.getPlayer(), "destroyed-hopper");

        // Drop the hopper as an item to save data in the item.
        Arrays.stream(hopper.getInventory().getContents())
                .filter(Objects::nonNull)
                .forEach(itemStack -> hopper.getWorld().dropItemNaturally(hopper.getLocation(), itemStack));

        event.setDropItems(false);

        final ItemStack item = this.hopperManager.getHopperAsItem(customHopper.get(), 1).clone();
        if (!plugin.getConfig().getBoolean("insta-pickup") || event.getPlayer().getInventory().firstEmpty() == -1) {
            event.getBlock().getWorld().dropItem(event.getBlock().getLocation(), item);
            return;
        }

        event.getPlayer().getInventory().addItem(item);
    }


}
