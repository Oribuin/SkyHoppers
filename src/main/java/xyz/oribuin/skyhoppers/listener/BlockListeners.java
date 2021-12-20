package xyz.oribuin.skyhoppers.listener;

import xyz.oribuin.skyhoppers.SkyHoppersPlugin;
import xyz.oribuin.skyhoppers.hook.BentoBoxHook;
import xyz.oribuin.skyhoppers.hook.WorldGuardHook;
import xyz.oribuin.skyhoppers.manager.DataManager;
import xyz.oribuin.skyhoppers.manager.HopperManager;
import xyz.oribuin.skyhoppers.manager.MessageManager;
import xyz.oribuin.skyhoppers.obj.SkyHopper;
import org.bukkit.block.Container;
import org.bukkit.block.Hopper;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

import static xyz.oribuin.skyhoppers.util.PluginUtils.getBlockLoc;

/**
 * @author Oribuin
 */
public class BlockListeners implements Listener {

    private final SkyHoppersPlugin plugin;
    private final DataManager data;
    private final MessageManager msg;
    private final HopperManager hopperManager;

    public BlockListeners(final SkyHoppersPlugin plugin) {
        this.plugin = plugin;
        this.data = plugin.getManager(DataManager.class);
        this.msg = plugin.getManager(MessageManager.class);
        this.hopperManager = plugin.getManager(HopperManager.class);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onHopperPlace(BlockPlaceEvent event) {

        final Player player = event.getPlayer();

        if (!(event.getBlock().getState() instanceof Hopper hopper)) {
            return;
        }

        final ItemStack itemInHand = event.getItemInHand();
        final Optional<SkyHopper> handHopper = this.hopperManager.getHopperFromItem(itemInHand);
        if (handHopper.isEmpty())
            return;

        if (!WorldGuardHook.buildAllowed(player, hopper.getLocation()) || !BentoBoxHook.buildAllowed(player, hopper.getLocation())) {
            this.msg.send(player, "cannot-use");
            event.setCancelled(true);
            return;
        }

        if (this.hopperManager.getHopperFromBlock(hopper).isPresent())
            return;

        handHopper.get().setLocation(getBlockLoc(event.getBlock().getLocation()));
        this.data.saveHopper(handHopper.get());
        this.hopperManager.saveHopper(handHopper.get());
        this.msg.send(event.getPlayer(), "placed-hopper");
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onHopperBreak(BlockBreakEvent event) {

        final Player player = event.getPlayer();
        if (!(event.getBlock().getState() instanceof Hopper hopper)) {
            return;
        }

        final Optional<SkyHopper> customHopper = this.hopperManager.getHopperFromBlock(hopper);
        if (customHopper.isEmpty())
            return;

        if (!WorldGuardHook.buildAllowed(player, hopper.getLocation()) || !BentoBoxHook.buildAllowed(player, hopper.getLocation())) {
            this.msg.send(player, "cannot-use");
            event.setCancelled(true);
            return;
        }

        // Remove the hopper from the visualizer.
        Map<UUID, SkyHopper> hopperViewers = this.plugin.getHopperViewers();
        hopperViewers.entrySet().removeIf(entry -> entry.getValue().getLocation() != null
                && getBlockLoc(entry.getValue().getLocation()).equals(getBlockLoc(hopper.getLocation())));

        // Delete the hopper's data
        this.data.deleteHopper(customHopper.get().getLocation());
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

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onLinkedBreak(BlockBreakEvent event) {

        final Player player = event.getPlayer();
        if (!(event.getBlock().getState() instanceof Container container))
            return;

        if (!SkyHopper.validContainers().contains(event.getBlock().getType()))
            return;

        final Optional<SkyHopper> linkedHopper = this.data.getCachedHoppers().values().stream()
                .filter(hopper -> hopper.getLinked() != null)
                .filter(hopper -> getBlockLoc(container.getLocation()).equals(getBlockLoc(hopper.getLinked().getLocation())))
                .findAny();

        if (linkedHopper.isEmpty())
            return;

        if (!WorldGuardHook.buildAllowed(player, container.getLocation()) || !BentoBoxHook.buildAllowed(player, container.getLocation())) {
            this.msg.send(player, "cannot-use");
            event.setCancelled(true);
            return;
        }

        this.msg.send(event.getPlayer(), "unlinked-container");
        linkedHopper.get().setLinked(null);
        hopperManager.saveHopper(linkedHopper.get());
    }

}
