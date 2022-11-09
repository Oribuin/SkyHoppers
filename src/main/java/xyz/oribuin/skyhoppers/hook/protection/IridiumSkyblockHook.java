package xyz.oribuin.skyhoppers.hook.protection;

import com.iridium.iridiumskyblock.PermissionType;
import com.iridium.iridiumskyblock.api.IridiumSkyblockAPI;
import com.iridium.iridiumskyblock.database.Island;
import com.iridium.iridiumskyblock.database.User;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Optional;

public class IridiumSkyblockHook implements ProtectionHook {

    @Override
    public boolean canBuild(Player player, Location location) {
        IridiumSkyblockAPI api = IridiumSkyblockAPI.getInstance();
        Optional<Island> island = api.getIslandViaLocation(location);
        if (island.isEmpty())
            return true;

        User user = api.getUser(player);
        return api.getIslandPermission(island.get(), user, PermissionType.BLOCK_PLACE) && api.getIslandPermission(island.get(), user, PermissionType.BLOCK_BREAK);
    }

    @Override
    public boolean canOpen(Player player, Location location) {
        IridiumSkyblockAPI api = IridiumSkyblockAPI.getInstance();
        Optional<Island> island = api.getIslandViaLocation(location);
        if (island.isEmpty())
            return true;

        User user = api.getUser(player);
        return api.getIslandPermission(island.get(), user, PermissionType.OPEN_CONTAINERS);
    }

}
