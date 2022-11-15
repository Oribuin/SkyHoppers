package xyz.oribuin.skyhoppers.command.command;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.RoseCommand;
import dev.rosewood.rosegarden.command.framework.RoseCommandWrapper;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.bukkit.Bukkit;
import xyz.oribuin.skyhoppers.hopper.SkyHopper;
import xyz.oribuin.skyhoppers.manager.HopperManager;
import xyz.oribuin.skyhoppers.manager.LocaleManager;

public class GiveAllCommand extends RoseCommand {

    public GiveAllCommand(RosePlugin rosePlugin, RoseCommandWrapper parent) {
        super(rosePlugin, parent);
    }

    @RoseExecutable
    public void execute(CommandContext context, Integer amount) {
        final var manager = this.rosePlugin.getManager(HopperManager.class);
        final var locale = this.rosePlugin.getManager(LocaleManager.class);

        int newAmount = Math.max(1, Math.min(64, amount));

        final StringPlaceholders placeholders = StringPlaceholders.builder("amount", newAmount)
                .addPlaceholder("players", Bukkit.getOnlinePlayers().size())
                .build();

        var itemStack = manager.getHopperAsItem(new SkyHopper(), newAmount);

        Bukkit.getOnlinePlayers().forEach(player -> player.getInventory().addItem(itemStack));
        locale.sendMessage(context.getSender(), "command-giveall-success", placeholders);
    }

    @Override
    protected String getDefaultName() {
        return "giveall";
    }

    @Override
    public String getDescriptionKey() {
        return "command-giveall-description";
    }

    @Override
    public String getRequiredPermission() {
        return "skyhoppers.giveall";
    }
}
