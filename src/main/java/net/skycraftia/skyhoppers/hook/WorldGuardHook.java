package net.skycraftia.skyhoppers.hook;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class WorldGuardHook implements ProtectionHook {

    @Override
    public boolean canBuild(Player player, Location loc) {
        if (!Bukkit.getPluginManager().isPluginEnabled("WorldGuard"))
            return true;

        final WorldGuard worldGuard = WorldGuard.getInstance();
        final RegionQuery query = worldGuard.getPlatform().getRegionContainer().createQuery();
        return query.testState(BukkitAdapter.adapt(loc), WorldGuardPlugin.inst().wrapPlayer(player), Flags.BUILD);
    }

    @Override
    public boolean canUse(Player player, Location loc) {
        if (!Bukkit.getPluginManager().isPluginEnabled("WorldGuard"))
            return true;

        final WorldGuard worldGuard = WorldGuard.getInstance();
        final RegionQuery query = worldGuard.getPlatform().getRegionContainer().createQuery();
        return query.testState(BukkitAdapter.adapt(loc), WorldGuardPlugin.inst().wrapPlayer(player), Flags.USE);
    }

}
