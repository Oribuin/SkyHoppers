package xyz.oribuin.skyhoppers.hook.protection;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.TownBlock;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class TownyAdvancedHook implements ProtectionHook {

    final TownyAPI api = TownyAPI.getInstance();

    @Override
    public boolean canBuild(Player player, Location location) {

        if (TownyAPI.getInstance().isWilderness(location))
            return true;

        TownBlock townBlock = api.getTownBlock(location);

        try {
            return townBlock != null && townBlock.getTown().hasResident(player.getUniqueId());
        } catch (NotRegisteredException e) {
            return false;
        }
    }

    @Override
    public boolean canOpen(Player player, Location location) {
        if (TownyAPI.getInstance().isWilderness(location))
            return true;

        TownBlock townBlock = api.getTownBlock(location);
        try {
            return townBlock != null && townBlock.getTown().hasResident(player.getUniqueId());
        } catch (NotRegisteredException e) {
            return false;
        }
    }

}
