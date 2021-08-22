package net.skycraftia.skyhoppers.command;

import net.skycraftia.skyhoppers.SkyHoppers;
import net.skycraftia.skyhoppers.gui.HopperGUI;
import net.skycraftia.skyhoppers.manager.HopperManager;
import net.skycraftia.skyhoppers.manager.MessageManager;
import net.skycraftia.skyhoppers.obj.CustomHopper;
import org.bukkit.block.Block;
import org.bukkit.block.Hopper;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.oribuin.orilibrary.command.SubCommand;

import java.util.Optional;

@SubCommand.Info(
        names = {"manage"},
        permission = "skyhoppers.manage",
        usage = "/hoppers manage"
)
public class SubManage extends SubCommand {

    private final SkyHoppers plugin = (SkyHoppers) this.getOriPlugin();
    private final MessageManager msg = this.plugin.getManager(MessageManager.class);
    private final HopperManager hopperManager = this.plugin.getManager(HopperManager.class);

    public SubManage(SkyHoppers plugin, HopperCommand command) {
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

        final Optional<CustomHopper> customHopper = this.hopperManager.getHopperFromBlock(hopperBlock);
        if (customHopper.isEmpty()) {
            this.msg.send(sender, "not-a-hopper");
            return;
        }

        // TODO, Protection Hooks such as BSkyblock, WorldGuard, ECT.
        new HopperGUI(plugin).create(customHopper.get(), player);

    }
}
