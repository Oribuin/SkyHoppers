package xyz.oribuin.skyhoppers.command;

import xyz.oribuin.skyhoppers.SkyHoppersPlugin;
import xyz.oribuin.skyhoppers.gui.HopperGUI;
import xyz.oribuin.skyhoppers.manager.HopperManager;
import xyz.oribuin.skyhoppers.manager.MessageManager;
import xyz.oribuin.skyhoppers.obj.SkyHopper;
import org.bukkit.block.Block;
import org.bukkit.block.Hopper;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.oribuin.orilibrary.command.SubCommand;

import java.util.Optional;

@SubCommand.Info(
        names = {"manage"},
        permission = "skyhoppers.use",
        usage = "/hoppers manage"
)
public class SubManage extends SubCommand {

    private final SkyHoppersPlugin plugin = (SkyHoppersPlugin) this.getOriPlugin();
    private final MessageManager msg = this.plugin.getManager(MessageManager.class);
    private final HopperManager hopperManager = this.plugin.getManager(HopperManager.class);

    public SubManage(SkyHoppersPlugin plugin, HopperCommand command) {
        super(plugin, command);
    }

    @Override
    public void executeArgument(CommandSender sender, String[] args) {

        if (!(sender instanceof Player player)) {
            this.msg.send(sender, "player-only");
            return;
        }

        final Block targetBlock = player.getTargetBlockExact(5);

        if (targetBlock == null || !(targetBlock.getState() instanceof Hopper hopperBlock)) {
            this.msg.send(sender, "not-a-hopper");
            return;
        }

        final Optional<SkyHopper> customHopper = this.hopperManager.getHopperFromBlock(hopperBlock);
        if (customHopper.isEmpty()) {
            this.msg.send(sender, "not-a-hopper");
            return;
        }

        if (!this.hopperManager.canOpen(player, targetBlock.getLocation())) {
            this.msg.send(player, "cannot-use");
            return;
        }

        new HopperGUI(plugin).create(customHopper.get(), player);

    }
}
