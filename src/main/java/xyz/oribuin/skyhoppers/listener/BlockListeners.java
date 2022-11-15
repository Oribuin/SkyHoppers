package xyz.oribuin.skyhoppers.listener;

import org.bukkit.block.Container;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import xyz.oribuin.skyhoppers.SkyHoppersPlugin;
import xyz.oribuin.skyhoppers.hopper.SkyHopper;
import xyz.oribuin.skyhoppers.manager.HookManager;
import xyz.oribuin.skyhoppers.manager.HopperManager;
import xyz.oribuin.skyhoppers.manager.LocaleManager;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static xyz.oribuin.skyhoppers.util.PluginUtils.getBlockLoc;

/**
 * @author Oribuin
 */
public class BlockListeners implements Listener {

    private final SkyHoppersPlugin plugin;
    private final HopperManager manager;
    private final HookManager hookManager;
    private final LocaleManager locale;

    public BlockListeners(final SkyHoppersPlugin plugin) {
        this.plugin = plugin;
        this.manager = plugin.getManager(HopperManager.class);
        this.hookManager = plugin.getManager(HookManager.class);
        this.locale = plugin.getManager(LocaleManager.class);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onHopperPlace(BlockPlaceEvent event) {

        final var player = event.getPlayer();

        if (!(event.getBlock().getState() instanceof org.bukkit.block.Hopper hopper)) {
            return;
        }

        final var itemInHand = event.getItemInHand();
        final var handHopper = this.manager.getHopper(itemInHand);
        if (handHopper == null)
            return;

        if (!hookManager.canBuild(player, hopper.getLocation())) {
            this.locale.sendMessage(player, "hopper-cannot-build");
            event.setCancelled(true);
            return;
        }

        if (this.manager.getHopper(hopper) != null) {
            this.locale.sendMessage(player, "hopper-already-placed");
            event.setCancelled(true);
            return;
        }

        this.manager.createHopper(event.getBlock(), player);
        this.locale.sendMessage(player, "hopper-placed-success");
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onHopperBreak(BlockBreakEvent event) {

        final var player = event.getPlayer();
        if (!(event.getBlock().getState() instanceof org.bukkit.block.Hopper hopper)) {
            return;
        }

        final var skyHopper = this.manager.getHopper(hopper);
        if (skyHopper == null)
            return;

        if (!hookManager.canBuild(player, hopper.getLocation())) {
            this.locale.sendMessage(player, "hopper-cannot-build");
            event.setCancelled(true);
            return;
        }

        if (!player.hasPermission("skyhoppers.admin")
                && skyHopper.getOwner() != null
                && !skyHopper.getOwner().equals(player.getUniqueId())) {
            this.locale.sendMessage(player, "hopper-not-owner");
            event.setCancelled(true);
            return;
        }


        // Remove the hopper from the visualizer.
        Map<UUID, SkyHopper> hopperViewers = this.manager.getHopperViewers();
        hopperViewers.entrySet().removeIf(entry -> entry.getValue().getLocation() != null
                && getBlockLoc(entry.getValue().getLocation()).equals(getBlockLoc(hopper.getLocation())));

        // Delete the hopper's data
        this.manager.removeHopper(skyHopper);
        this.locale.sendMessage(player, "hopper-removed-success");

        // Drop the hopper as an item to save data in the item.
        Arrays.stream(hopper.getInventory().getContents())
                .filter(Objects::nonNull)
                .forEach(itemStack -> hopper.getWorld().dropItemNaturally(hopper.getLocation(), itemStack));

        event.setDropItems(false);

        final var item = this.manager.getHopperAsItem(skyHopper, 1).clone();
        if (!plugin.getConfig().getBoolean("insta-pickup") || event.getPlayer().getInventory().firstEmpty() == -1) {
            event.getBlock().getWorld().dropItem(event.getBlock().getLocation(), item);
            return;
        }

        event.getPlayer().getInventory().addItem(item);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onLinkedBreak(BlockBreakEvent event) {

        final var player = event.getPlayer();
        if (!(event.getBlock().getState() instanceof Container container))
            return;

        var skyHopper = this.manager.getHopperFromContainer(container);
        if (skyHopper == null)
            return;

        event.setCancelled(true);

        if (!player.hasPermission("skyhoppers.admin")
                && skyHopper.getOwner() != null
                && !skyHopper.getOwner().equals(player.getUniqueId())) {
            this.locale.sendMessage(player, "hopper-not-owner");
            return;
        }


        if (!hookManager.canBuild(player, container.getLocation())) {
            this.locale.sendMessage(player, "hopper-cannot-build");
            return;
        }

        skyHopper.setLinked(null);
        this.manager.saveHopperBlock(skyHopper);
        this.locale.sendMessage(player, "hopper-unlinked-success");
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onHopperLink(PlayerInteractEvent event) {
        final var player = event.getPlayer();
        final var block = event.getClickedBlock();

        if (!event.hasBlock() || event.getAction() != Action.LEFT_CLICK_BLOCK || block == null)
            return;

        if (!(block.getState() instanceof Container container))
            return;

        if (!this.plugin.getLinkingPlayers().containsKey(player.getUniqueId()))
            return;

        event.setCancelled(true);

        if (!this.hookManager.canOpen(player, block.getLocation())) {
            this.locale.sendMessage(player, "hopper-cannot-open");
            return;
        }

        if (this.manager.getHopper(block.getLocation()) != null) {
            this.locale.sendMessage(player, "hopper-already-placed");
            return;
        }

        final var skyHopper = this.plugin.getLinkingPlayers().get(player.getUniqueId());
        if (skyHopper == null)
            return;

        if (!player.hasPermission("skyhoppers.admin")
                && skyHopper.getOwner() != null
                && !skyHopper.getOwner().equals(player.getUniqueId())) {
            this.locale.sendMessage(player, "hopper-not-owner");
            return;
        }

        this.plugin.getLinkingPlayers().remove(player.getUniqueId());

        if (skyHopper.getLinked() != null && skyHopper.getLinked().getBlock().getLocation().equals(container.getBlock().getLocation())) {
            skyHopper.setLinked(null);
            this.manager.saveHopperBlock(skyHopper);
            this.locale.sendMessage(player, "hopper-unlinked-success");
            return;
        }

        skyHopper.setLinked(container);
        this.manager.saveHopperBlock(skyHopper);
        locale.sendMessage(player, "hopper-linked-success");
    }

}
