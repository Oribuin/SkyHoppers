package net.skycraftia.skyhoppers.listener;

import net.skycraftia.skyhoppers.SkyHoppers;
import net.skycraftia.skyhoppers.gui.HopperGUI;
import net.skycraftia.skyhoppers.manager.HopperManager;
import net.skycraftia.skyhoppers.obj.CustomHopper;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.block.Hopper;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.Optional;

public class PlayerListeners implements Listener {

    private final SkyHoppers plugin;
    private final HopperManager hopperManager;

    public PlayerListeners(final SkyHoppers plugin) {
        this.plugin = plugin;
        this.hopperManager = this.plugin.getManager(HopperManager.class);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onInteract(PlayerInteractEvent event) {

        final Player player = event.getPlayer();

        if (!event.hasBlock() || event.getAction() != Action.LEFT_CLICK_BLOCK)
            return;

        final Block block = event.getClickedBlock();
        if (block == null)
            return;

        if (!(block.getState() instanceof Hopper hopper))
            return;

        final Optional<CustomHopper> customHopper = this.hopperManager.getHopperFromBlock(hopper);
        if (customHopper.isEmpty())
            return;

        if (player.isSneaking())
            return;

        event.setCancelled(true);

        if (this.plugin.getLinkingPlayers().containsKey(player.getUniqueId())) {
            return;
        }

        new HopperGUI(plugin).create(customHopper.get(), player);

    }
}
