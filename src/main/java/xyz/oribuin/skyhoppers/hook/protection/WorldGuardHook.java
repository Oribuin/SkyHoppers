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
        return query.testState(BukkitAdapter.adapt(location), WorldGuardPlugin.inst().wrapPlayer(player), Flags.BUILD);
    }

    @Override
    public boolean canOpen(Player player, Location location) {
        final var query = worldGuard.getPlatform().getRegionContainer().createQuery();
        return query.testState(BukkitAdapter.adapt(location), WorldGuardPlugin.inst().wrapPlayer(player), Flags.CHEST_ACCESS);
    }
}
