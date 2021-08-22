package net.skycraftia.skyhoppers.command;

import net.skycraftia.skyhoppers.SkyHoppers;
import net.skycraftia.skyhoppers.manager.HopperManager;
import net.skycraftia.skyhoppers.manager.MessageManager;
import net.skycraftia.skyhoppers.obj.CustomHopper;
import net.skycraftia.skyhoppers.util.PluginUtils;
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
        permission = "skyhoppers.view",
        usage = "/hoppers view"
)
public class SubView extends SubCommand {

    private final SkyHoppers plugin = (SkyHoppers) this.getOriPlugin();
    private final MessageManager msg = this.plugin.getManager(MessageManager.class);
    private final HopperManager hopperManager = this.plugin.getManager(HopperManager.class);

    public SubView(SkyHoppers plugin, HopperCommand command) {
        super(plugin, command);
    }

    @Override
    public void executeArgument(CommandSender sender, String[] args) {

        if (!(sender instanceof Player player)) {
            this.msg.send(sender, "player-only");
            return;
        }

        final Block targetBlock = player.getTargetBlockExact(5);

        final Map<UUID, CustomHopper> hopperMap = this.plugin.getHopperViewTask().getHopperViewers();

        if (targetBlock == null || !(targetBlock.getState() instanceof Hopper hopperBlock) || targetBlock.getType() == Material.AIR) {
            if (hopperMap.containsKey(player.getUniqueId())) {
                hopperMap.remove(player.getUniqueId());
                this.msg.send(sender, "toggled-off-visualiser");
                return;
            }

            this.msg.send(sender, "not-a-hopper");
            return;
        }

        final Optional<CustomHopper> customHopper = this.hopperManager.getHopperFromBlock(hopperBlock);
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
