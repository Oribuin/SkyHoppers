package xyz.oribuin.skyhoppers.util;

import dev.rosewood.rosegarden.utils.HexUtils;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.apache.commons.lang3.StringUtils;
import xyz.oribuin.skyhoppers.obj.SkyHopper;
import org.bukkit.Location;
import org.bukkit.block.Container;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.stream.Collectors;

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
     * @param skyHopper The custom hopper with the item filter
     * @return true if the item has been filtered.
     */
    public static boolean itemFiltered(ItemStack item, SkyHopper skyHopper) {
        switch (skyHopper.getFilterType()) {
            case WHITELIST -> {
                return !skyHopper.getFilterItems().contains(item.getType());
            }

            case BLACKLIST, DESTROY -> {
                return skyHopper.getFilterItems().contains(item.getType());
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

    public static String format(String text, StringPlaceholders placeholders) {
        return HexUtils.colorify(placeholders.apply(text));
    }

    public static List<String> format(List<String> text, StringPlaceholders placeholders) {
        return text.stream().map(line -> format(line, placeholders)).collect(Collectors.toList());
    }

    public static String formatEnum(String text) {
        return StringUtils.capitalize(text.replace("_", " ").toLowerCase());
    }
}
