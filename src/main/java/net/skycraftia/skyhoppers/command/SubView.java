package net.skycraftia.skyhoppers.command;

import net.skycraftia.skyhoppers.SkyHoppersPlugin;
import net.skycraftia.skyhoppers.manager.HopperManager;
import net.skycraftia.skyhoppers.manager.MessageManager;
import net.skycraftia.skyhoppers.obj.SkyHopper;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Hopper;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.oribuin.orilibrary.command.SubCommand;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@SubCommand.Info(
        names = {"view"},
        permission = "skyhoppers.use",
        usage = "/hoppers view"
)
public class SubView extends SubCommand {

    private final SkyHoppersPlugin plugin = (SkyHoppersPlugin) this.getOriPlugin();
    private final MessageManager msg = this.plugin.getManager(MessageManager.class);
    private final HopperManager hopperManager = this.plugin.getManager(HopperManager.class);

    public SubView(SkyHoppersPlugin plugin, HopperCommand command) {
        super(plugin, command);
    }

    @Override
    public void executeArgument(CommandSender sender, String[] args) {

        if (!(sender instanceof Player player)) {
            this.msg.send(sender, "player-only");
            return;
        }

        final Block targetBlock = player.getTargetBlockExact(5);

        final Map<UUID, SkyHopper> hopperMap = this.plugin.getHopperViewers();

        if (targetBlock == null || !(targetBlock.getState() instanceof Hopper hopperBlock) || targetBlock.getType() == Material.AIR) {
            if (hopperMap.containsKey(player.getUniqueId())) {
                hopperMap.remove(player.getUniqueId());
                this.msg.send(sender, "toggled-off-visualiser");
                return;
            }

            this.msg.send(sender, "not-a-hopper");
            return;
        }

        final Optional<SkyHopper> customHopper = this.hopperManager.getHopperFromBlock(hopperBlock);
        if (customHopper.isEmpty()) {
            this.msg.send(sender, "not-a-hopper");
            return;
        }

        if (hopperMap.containsKey(player.getUniqueId())) {
            this.msg.send(sender, "changed-visualiser");
        } else {
            this.msg.send(sender, "toggled-on-visualiser");
        }

        hopperMap.put(player.getUniqueId(), customHopper.get());
    }
}
