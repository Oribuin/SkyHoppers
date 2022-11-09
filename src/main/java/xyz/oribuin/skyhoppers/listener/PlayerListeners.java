package xyz.oribuin.skyhoppers.listener;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import xyz.oribuin.skyhoppers.SkyHoppersPlugin;
import xyz.oribuin.skyhoppers.gui.HopperGUI;
import xyz.oribuin.skyhoppers.manager.HookManager;
import xyz.oribuin.skyhoppers.manager.HopperManager;
import xyz.oribuin.skyhoppers.manager.LocaleManager;
import xyz.oribuin.skyhoppers.obj.SkyHopper;

public class PlayerListeners implements Listener {

    private final SkyHoppersPlugin plugin;
    private final HopperManager manager;
    private final LocaleManager locale;
    private final HookManager hookManager;

    public PlayerListeners(final SkyHoppersPlugin plugin) {
        this.plugin = plugin;
        this.manager = this.plugin.getManager(HopperManager.class);
        this.locale = this.plugin.getManager(LocaleManager.class);
        this.hookManager = this.plugin.getManager(HookManager.class);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onHopperMenuOpen(PlayerInteractEvent event) {

        final Player player = event.getPlayer();

        if (!event.hasBlock() || event.getAction() != Action.LEFT_CLICK_BLOCK)
            return;

        final Block block = event.getClickedBlock();
        if (block == null)
            return;

        if (!(block.getState() instanceof org.bukkit.block.Hopper hopperBlock))
            return;

        final SkyHopper hopper = this.manager.getHopper(hopperBlock);
        if (hopper == null || player.isSneaking())
            return;

        event.setCancelled(true);

        if (!this.hookManager.canOpen(player, block.getLocation())) {
            this.locale.sendMessage(player, "hopper-cannot-open");
            return;
        }

        if (this.plugin.getLinkingPlayers().containsKey(player.getUniqueId())) {
            return;
        }

        new HopperGUI(this.plugin).openGUI(player, hopper);
    }

}
