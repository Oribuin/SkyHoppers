package net.skycraftia.skyhoppers.util;

import net.skycraftia.skyhoppers.obj.CustomHopper;
import org.bukkit.Location;
import org.bukkit.block.Container;
import org.bukkit.inventory.ItemStack;

public final class PluginUtils {

    /**
     * Format a location into a readable String.
     *
     * @param loc The location
     * @return The formatted Location.
     */
    public static String formatLocation(Location loc) {
        if (loc == null)
            return "None";

        return loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ();
    }

    /**
     * Format a container into the container's location.
     *
     * @param container The container
     * @return The formatted Location.
     */
    public static String formatContainerLoc(Container container) {
        if (container == null)
            return "None";

        return formatLocation(container.getLocation());

    }

    /**
     * Check if an item is available through the filter.
     *
     * @param item   The item being filtered.
     * @param hopper The custom hopper with the item filter
     * @return true if the item has been filtered.
     */
    public static boolean itemFiltered(ItemStack item, CustomHopper hopper) {
        switch (hopper.getFilterType()) {
            case WHITELIST -> {
                return !hopper.getFilterItems().contains(item.getType());
            }

            case BLACKLIST, DESTROY -> {
                return hopper.getFilterItems().contains(item.getType());
            }

        }

        return false;
    }

    /**
     * Get the block location of the location.;
     *
     * @param loc The location;
     * @return The block location
     */
    public static Location getBlockLoc(Location loc) {
        final Location location = loc.clone();
        return new Location(location.getWorld(), location.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    }

    /**
     * Center a location to the center of the block.
     *
     * @param location The location to be centered.
     * @return The centered location.
     */
    public static Location centerLocation(Location location) {
        final Location loc = location.clone();
        loc.add(0.5, 0.5, 0.5);
        loc.setYaw(180f);
        loc.setPitch(0f);

        return loc;
    }

}
