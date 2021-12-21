package xyz.oribuin.skyhoppers.hook.protection;

import com.iridium.iridiumskyblock.PermissionType;
import com.iridium.iridiumskyblock.api.IridiumSkyblockAPI;
import com.iridium.iridiumskyblock.database.Island;
import com.iridium.iridiumskyblock.database.User;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import xyz.oribuin.skyhoppers.hook.ProtectionHook;

import java.util.Optional;

public class IridiumHook implements ProtectionHook {

    @Override
    public boolean canBuild(Player player, Location location) {
        if (player.hasPermission("skyhoppers.bypass"))
            return true;

        if (Bukkit.getPluginManager().isPluginEnabled("IridiumSkyblock"))
            return true;

        IridiumSkyblockAPI api = IridiumSkyblockAPI.getInstance();
        Optional<Island> island = api.getIslandViaLocation(location);
        if (island.isEmpty())
            return true;

        User user = api.getUser(player);
        return api.getIslandPermission(island.get(), user, PermissionType.BLOCK_PLACE);
    }

    @Override
    public boolean canOpen(Player player, Location location) {
        if (player.hasPermission("skyhoppers.bypass"))
            return true;

        if (Bukkit.getPluginManager().isPluginEnabled("IridiumSkyblock"))
            return true;

        IridiumSkyblockAPI api = IridiumSkyblockAPI.getInstance();
        Optional<Island> island = api.getIslandViaLocation(location);
        if (island.isEmpty())
            return true;

        User user = api.getUser(player);
        return api.getIslandPermission(island.get(), user, PermissionType.OPEN_CONTAINERS);
    }
}
