package xyz.oribuin.skyhoppers.hook.protection;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flags;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class WorldGuardHook implements ProtectionHook {

    final WorldGuard worldGuard = WorldGuard.getInstance();

    @Override
    public boolean canBuild(Player player, Location location) {
        final var query = worldGuard.getPlatform().getRegionContainer().createQuery();
        final var wgLocation = BukkitAdapter.adapt(location);
        final var wgPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
        final var world = BukkitAdapter.adapt(location.getWorld());

        if (WorldGuard.getInstance().getPlatform().getSessionManager().hasBypass(wgPlayer, world)) {
            return true;
        }

        return query.testState(wgLocation, wgPlayer, Flags.BLOCK_BREAK) && query.testState(wgLocation, wgPlayer, Flags.BLOCK_PLACE);
    }

    @Override
    public boolean canOpen(Player player, Location location) {
        final var query = worldGuard.getPlatform().getRegionContainer().createQuery();
        final var wgLocation = BukkitAdapter.adapt(location);
        final var wgPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
        final var world = BukkitAdapter.adapt(location.getWorld());

        if (WorldGuard.getInstance().getPlatform().getSessionManager().hasBypass(wgPlayer, world)) {
            return true;
        }

        return query.testState(BukkitAdapter.adapt(location), WorldGuardPlugin.inst().wrapPlayer(player), Flags.CHEST_ACCESS);
    }

}
