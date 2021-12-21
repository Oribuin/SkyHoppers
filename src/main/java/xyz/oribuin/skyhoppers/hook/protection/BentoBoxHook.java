package xyz.oribuin.skyhoppers.hook.protection;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import world.bentobox.bentobox.BentoBox;
import world.bentobox.bentobox.api.user.User;
import world.bentobox.bentobox.database.objects.Island;
import world.bentobox.bentobox.lists.Flags;
import xyz.oribuin.skyhoppers.hook.ProtectionHook;

import java.util.Optional;

public class BentoBoxHook implements ProtectionHook {

    @Override
    public boolean canBuild(Player player, Location location) {
        if (player.hasPermission("skyhoppers.bypass"))
            return true;

        if (!Bukkit.getPluginManager().isPluginEnabled("BentoBox"))
            return true;

        Optional<Island> optionalIsland = BentoBox.getInstance().getIslandsManager().getIslandAt(location);
        if (optionalIsland.isEmpty())
            return true;

        Island island = optionalIsland.get();
        User user = BentoBox.getInstance().getPlayersManager().getUser(player.getUniqueId());
        return island.isAllowed(user, Flags.BREAK_BLOCKS) && island.isAllowed(user, Flags.PLACE_BLOCKS);
    }

    @Override
    public boolean canOpen(Player player, Location location) {
        if (player.hasPermission("skyhoppers.bypass"))
            return true;

        if (!Bukkit.getPluginManager().isPluginEnabled("BentoBox"))
            return true;

        Optional<Island> optionalIsland = BentoBox.getInstance().getIslandsManager().getIslandAt(location);
        if (optionalIsland.isEmpty())
            return true;

        Island island = optionalIsland.get();
        User user = BentoBox.getInstance().getPlayersManager().getUser(player.getUniqueId());
        return island.isAllowed(user, Flags.CONTAINER);
    }
}
