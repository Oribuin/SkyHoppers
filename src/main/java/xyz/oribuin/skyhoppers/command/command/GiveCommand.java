package xyz.oribuin.skyhoppers.command.command;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.RoseCommand;
import dev.rosewood.rosegarden.command.framework.RoseCommandWrapper;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.bukkit.entity.Player;
import xyz.oribuin.skyhoppers.hopper.SkyHopper;
import xyz.oribuin.skyhoppers.manager.HopperManager;
import xyz.oribuin.skyhoppers.manager.LocaleManager;

public class GiveCommand extends RoseCommand {

    public GiveCommand(RosePlugin rosePlugin, RoseCommandWrapper parent) {
        super(rosePlugin, parent);
    }

    @RoseExecutable
    public void execute(CommandContext context, Player player, Integer amount) {
        final var manager = this.rosePlugin.getManager(HopperManager.class);
        final var locale = this.rosePlugin.getManager(LocaleManager.class);

        var newAmount = Math.max(1, Math.min(64, amount));

        final var placeholders = StringPlaceholders.builder("amount", newAmount)
                .addPlaceholder("player", player.getName())
                .build();

        var itemStack = manager.getHopperAsItem(new SkyHopper(), newAmount);
        player.getInventory().addItem(itemStack);
        locale.sendMessage(context.getSender(), "command-give-success", placeholders);
    }

    @Override
    protected String getDefaultName() {
        return "give";
    }

    @Override
    public String getDescriptionKey() {
        return "command-give-description";
    }

    @Override
    public String getRequiredPermission() {
        return "skyhoppers.give";
    }
}
