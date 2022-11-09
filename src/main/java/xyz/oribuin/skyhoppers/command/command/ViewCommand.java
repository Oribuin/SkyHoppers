package xyz.oribuin.skyhoppers.command.command;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.RoseCommand;
import dev.rosewood.rosegarden.command.framework.RoseCommandWrapper;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import org.bukkit.Material;
import org.bukkit.block.Hopper;
import org.bukkit.entity.Player;
import xyz.oribuin.skyhoppers.SkyHoppersPlugin;
import xyz.oribuin.skyhoppers.manager.HopperManager;
import xyz.oribuin.skyhoppers.manager.LocaleManager;
import xyz.oribuin.skyhoppers.obj.SkyHopper;

public class ViewCommand extends RoseCommand {

    public ViewCommand(RosePlugin rosePlugin, RoseCommandWrapper parent) {
        super(rosePlugin, parent);
    }

    @RoseExecutable
    public void execute(CommandContext context) {
        final var locale = this.rosePlugin.getManager(LocaleManager.class);
        final var manager = this.rosePlugin.getManager(HopperManager.class);

        final var player = (Player) context.getSender();
        final var block = player.getTargetBlock(5);
        final var hoppers = manager.getHopperViewers();

        // Disable the visualizer if the player is not looking at a skyhopper.
        if (block == null || (!(block.getState() instanceof Hopper)) || block.getType() == Material.AIR) {

            if (hoppers.containsKey(player.getUniqueId())) {
                hoppers.remove(player.getUniqueId());
                locale.sendMessage(player, "command-view-disabled");
                return;
            }

            locale.sendMessage(player, "command-view-not-skyhopper");
            return;
        }

        // Enable the visualizer if the player is looking at a skyhopper.
        SkyHopper hopper = manager.getHopper(block);
        if (hopper == null) {
            locale.sendMessage(player, "command-view-not-skyhopper");
            return;
        }

        if (hoppers.containsKey(player.getUniqueId())) {
            hoppers.remove(player.getUniqueId());
            locale.sendMessage(player, "command-view-disabled");
            return;
        }

        hoppers.put(player.getUniqueId(), hopper);
        locale.sendMessage(player, "command-view-success");
    }

    @Override
    protected String getDefaultName() {
        return "view";
    }

    @Override
    public String getDescriptionKey() {
        return "command-view-description";
    }

    @Override
    public String getRequiredPermission() {
        return "skyhoppers.view";
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }
}
