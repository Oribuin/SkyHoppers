package net.skycraftia.skyhoppers.hook;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import world.bentobox.bentobox.BentoBox;
import world.bentobox.bentobox.api.user.User;
import world.bentobox.bentobox.database.objects.Island;
import world.bentobox.bentobox.lists.Flags;

import java.util.Optional;

public class BentoBoxHook {

    /**
     * Check if a player can build on the island they are on.
     *
     * @param player The player who is building
     * @param loc    The location of the block they're placing / breaking
     * @return true if the player can build
     */
    public static boolean buildAllowed(Player player, Location loc) {
        if (!Bukkit.getPluginManager().isPluginEnabled("BentoBox"))
            return true;

        if (player.hasPermission("skyhoppers.bypass"))
            return true;

        Optional<Island> optionalIsland = BentoBox.getInstance().getIslandsManager().getIslandAt(loc);
        if (optionalIsland.isEmpty())
            return true;

        Island island = optionalIsland.get();
        User user = BentoBox.getInstance().getPlayersManager().getUser(player.getUniqueId());
        return island.isAllowed(user, Flags.BREAK_BLOCKS) && island.isAllowed(user, Flags.PLACE_BLOCKS);
    }

    /**
     * Check if a player can access the container they're trying to open on an island.
     *
     * @param player The player who is accessing the container.
     * @param loc    The location of the container.
     * @return true if the player can open the container.
     */
    public static boolean containerAllowed(Player player, Location loc) {
        if (!Bukkit.getPluginManager().isPluginEnabled("BentoBox"))
            return true;

        if (player.hasPermission("skyhoppers.bypass"))
            return true;

        Optional<Island> optionalIsland = BentoBox.getInstance().getIslandsManager().getIslandAt(loc);
        if (optionalIsland.isEmpty())
            return true;

        Island island = optionalIsland.get();
        User user = BentoBox.getInstance().getPlayersManager().getUser(player.getUniqueId());
        return island.isAllowed(user, Flags.CONTAINER);
    }

}
