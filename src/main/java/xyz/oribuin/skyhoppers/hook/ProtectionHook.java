package xyz.oribuin.skyhoppers.hook;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface ProtectionHook {

    /**
     * Check if a player can build at a specific location
     *
     * @param player The player who is building
     * @param location    The location of the block they're placing / breaking
     * @return true if the player can build
     */
    boolean canBuild(Player player, Location location);

    /**
     * Check if a player can access the container at a specified location
     *
     * @param player The player who is accessing the container.
     * @param location    The location of the container.
     * @return true if the player can open the container.
     */
    boolean canOpen(Player player, Location location);


}
