package xyz.oribuin.skyhoppers.command;

import xyz.oribuin.skyhoppers.SkyHoppersPlugin;
import xyz.oribuin.skyhoppers.manager.HopperManager;
import xyz.oribuin.skyhoppers.manager.MessageManager;
import xyz.oribuin.skyhoppers.obj.SkyHopper;
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

    private final SkyHoppersPlugin plugin = (SkyHoppersPlugin) this.getOriPlugin();
    private final MessageManager msg = this.plugin.getManager(MessageManager.class);
    private final HopperManager hopperManager = this.plugin.getManager(HopperManager.class);

    public SubGive(SkyHoppersPlugin plugin, HopperCommand command) {
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

        this.msg.send(player, "given-hopper", StringPlaceholders.single("amount", amount));
        this.msg.send(sender, "gave-hopper", StringPlaceholders.builder("amount", amount).addPlaceholder("player", player.getName()).build());
        final ItemStack item = this.hopperManager.getHopperAsItem(new SkyHopper(), amount);
        final Inventory inv = player.getInventory();
        if (inv.firstEmpty() == -1) {
            player.getWorld().dropItem(player.getLocation(), item);
            inv.addItem();
            return;
        }

        inv.addItem(item);
    }

}
