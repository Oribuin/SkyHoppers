package net.skycraftia.skyhoppers.hook;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import world.bentobox.bentobox.BentoBox;
import world.bentobox.bentobox.database.objects.Island;

import java.util.Optional;

public class BentoBoxHook implements ProtectionHook {

    @Override
    public boolean canBuild(Player player, Location loc) {
        if (!Bukkit.getPluginManager().isPluginEnabled("BentoBox"))
            return true;


        // Get the island at the block location.
        final Optional<Island> islandOptional = BentoBox.getInstance().getIslandsManager().getProtectedIslandAt(loc);
        if (islandOptional.isEmpty())
            return true;

        // Check if the block is in the island range, Could be a redundant check
        if (!islandOptional.get().inIslandSpace(loc))
            return true;

        // Get the player's current island rank.
        int rank = islandOptional.get().getRank(player.getUniqueId());
        islandOptional.get().getFlag(rank);
    }

    @Override
    public boolean canUse(Player player, Location loc) {
        return false;
    }

}
