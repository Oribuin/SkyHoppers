package xyz.oribuin.skyhoppers.hook.protection;

import com.iridium.iridiumskyblock.PermissionType;
import com.iridium.iridiumskyblock.api.IridiumSkyblockAPI;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class IridiumSkyblockHook implements ProtectionHook {

    IridiumSkyblockAPI api = IridiumSkyblockAPI.getInstance();

    @Override
    public boolean canBuild(Player player, Location location) {
        var island = api.getIslandViaLocation(location);
        if (island.isEmpty())
            return true;

        var user = api.getUser(player);
        return api.getIslandPermission(island.get(), user, PermissionType.BLOCK_PLACE) && api.getIslandPermission(island.get(), user, PermissionType.BLOCK_BREAK);
    }

    @Override
    public boolean canOpen(Player player, Location location) {
        var island = api.getIslandViaLocation(location);
        if (island.isEmpty())
            return true;

        var user = api.getUser(player);
        return api.getIslandPermission(island.get(), user, PermissionType.OPEN_CONTAINERS);
    }

}
