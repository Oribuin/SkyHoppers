package xyz.oribuin.skyhoppers.hook.protection;

import me.angeschossen.lands.api.flags.Flags;
import me.angeschossen.lands.api.integration.LandsIntegration;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import xyz.oribuin.skyhoppers.SkyHoppersPlugin;

public class LandsHook implements ProtectionHook {

    private final LandsIntegration integration = new LandsIntegration(SkyHoppersPlugin.getInstance());

    final
    @Override
    public boolean canBuild(Player player, Location location) {
        var area = integration.getArea(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ());

        if (area == null)
            return true;

        return area.hasFlag(player.getUniqueId(), Flags.BLOCK_PLACE) && area.hasFlag(player.getUniqueId(), Flags.BLOCK_BREAK);
    }

    @Override
    public boolean canOpen(Player player, Location location) {
        var area = integration.getArea(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ());

        if (area == null)
            return true;

        return area.hasFlag(player.getUniqueId(), Flags.INTERACT_CONTAINER);
    }


}
