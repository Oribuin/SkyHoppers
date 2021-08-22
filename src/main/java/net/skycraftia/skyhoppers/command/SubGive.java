package net.skycraftia.skyhoppers.command;

import net.skycraftia.skyhoppers.SkyHoppers;
import net.skycraftia.skyhoppers.manager.HopperManager;
import net.skycraftia.skyhoppers.manager.MessageManager;
import net.skycraftia.skyhoppers.obj.CustomHopper;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import xyz.oribuin.orilibrary.command.SubCommand;
import xyz.oribuin.orilibrary.util.StringPlaceholders;

@SubCommand.Info(
        names = {"give"},
        permission = "skyhoppers.give",
        usage = "/hoppers give <player> <amount>"
)
public class SubGive extends SubCommand {

    private final SkyHoppers plugin = (SkyHoppers) this.getOriPlugin();
    private final MessageManager msg = this.plugin.getManager(MessageManager.class);
    private final HopperManager hopperManager = this.plugin.getManager(HopperManager.class);

    public SubGive(SkyHoppers plugin, HopperCommand command) {
        super(plugin, command);
    }

    @Override
    public void executeArgument(CommandSender sender, String[] args) {

        // check argument length
        if (args.length != 3) {
            this.msg.send(sender, "invalid-args", StringPlaceholders.single("usage", this.getInfo().usage()));
            return;
        }

        // Check if the player exists
        final Player player = Bukkit.getPlayer(args[1]);
        if (player == null) {
            this.msg.send(sender, "invalid-player");
            return;
        }

        // Define the amount of hoppers
        int amount;
        try {
            amount = Integer.parseInt(args[2]);
        } catch (NumberFormatException ex) {
            this.msg.send(sender, "invalid-amount");
            return;
        }

        // Check if amount is less or equal to  0
        if (amount <= 0) {
            this.msg.send(sender, "invalid-amount");
            return;
        }

        this.msg.send(sender, "given-hopper", StringPlaceholders.single("amount", amount));
        final ItemStack item = this.hopperManager.getHopperAsItem(new CustomHopper(), amount);
        final Inventory inv = player.getInventory();
        if (inv.firstEmpty() == -1) {
            player.getWorld().dropItem(player.getLocation(), item);
            inv.addItem();
            return;
        }

        inv.addItem(item);
    }

}
