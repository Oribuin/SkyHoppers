package xyz.oribuin.skyhoppers.util;

import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import dev.rosewood.rosegarden.config.CommentedFileConfiguration;
import dev.rosewood.rosegarden.utils.HexUtils;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Container;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import xyz.oribuin.skyhoppers.hook.PAPI;
import xyz.oribuin.skyhoppers.hopper.SkyHopper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
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
     * @param item      The item being filtered.
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

    /**
     * Get ItemStack from CommentedFileSection path
     *
     * @param config       The CommentedFileSection
     * @param path         The path to the item
     * @param player       The player
     * @param placeholders The placeholders
     * @return The itemstack
     */
    public static ItemStack getItemStack(CommentedConfigurationSection config, String path, Player player, StringPlaceholders placeholders) {

        String materialName = get(config, path + ".material", null);
        Material material = Material.STONE;

        if (materialName != null) {
            material = Material.matchMaterial(materialName);

            if (material == null) {
                material = Material.STONE;
            }
        }

        // Format the item lore
        List<String> lore = get(config, path + ".lore", List.of());
        lore = lore.stream().map(s -> format(player, s, placeholders)).collect(Collectors.toList());

        // Get item flags
        ItemFlag[] flags = get(config, path + ".flags", new ArrayList<String>())
                .stream()
                .map(String::toUpperCase)
                .map(ItemFlag::valueOf)
                .toArray(ItemFlag[]::new);

        // Build the item stack
        ItemBuilder builder = new ItemBuilder(material)
                .setName(format(player, (String) get(config, path + ".name", null), placeholders))
                .setLore(lore)
                .setAmount(Math.max(get(config, path + ".amount", 1), 1))
                .setFlags(flags)
                .glow(get(config, path + ".glow", false))
                .setTexture(get(config, path + ".texture", null))
                .setModel(get(config, path + ".model-data", -1));

        // Get item owner
        String owner = get(config, path + ".owner", null);
        if (owner != null)
            builder.setOwner(Bukkit.getOfflinePlayer(UUID.fromString(owner)));

        // Get item enchantments
        final CommentedConfigurationSection enchants = config.getConfigurationSection(path + "enchants");
        if (enchants != null) {
            enchants.getKeys(false).forEach(key -> {
                Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(key));
                if (enchantment == null)
                    return;

                builder.addEnchant(enchantment, enchants.getInt(key));
            });
        }

        return builder.create();
    }

    /**
     * Get ItemStack from CommentedFileSection path
     *
     * @param config The CommentedFileSection
     * @param path   The path to the item
     * @return The itemstack
     */
    public static ItemStack getItemStack(CommentedConfigurationSection config, String path) {
        return getItemStack(config, path, null, StringPlaceholders.empty());
    }

    public static ItemStack getItemStack(CommentedConfigurationSection config, String path, Player player) {
        return getItemStack(config, path, player, StringPlaceholders.empty());
    }

    public static String format(String text) {
        return format(null, text, StringPlaceholders.empty());
    }

    /**
     * Format a string with placeholders and color codes
     *
     * @param player The player to format the string for
     * @param text   The string to format
     * @return The formatted string
     */
    public static String format(Player player, String text) {
        return format(player, text, StringPlaceholders.empty());
    }

    /**
     * Format a string with placeholders and color codes
     *
     * @param player       The player to format the string for
     * @param text         The text to format
     * @param placeholders The placeholders to replace
     * @return The formatted string
     */
    public static String format(Player player, String text, StringPlaceholders placeholders) {
        if (text == null)
            return null;

        return HexUtils.colorify(PAPI.apply(player, placeholders.apply(text)));
    }

    public static List<String> format(Player player, List<String> text, StringPlaceholders placeholders) {
        return text.stream().map(s -> format(player, s, placeholders)).collect(Collectors.toList());
    }

    public static String formatEnum(String text) {
        return StringUtils.capitalize(text.replace("_", " ").toLowerCase());
    }

    /**
     * Parse a list of strings from 1-1 to a stringlist
     *
     * @param list The list to parse
     * @return The parsed list
     */
    public static List<Integer> parseList(List<String> list) {
        List<Integer> newList = new ArrayList<>();
        for (String s : list) {
            if (!s.contains("-")) {
                newList.add(Integer.parseInt(s));
                continue;
            }

            String[] split = s.split("-");
            if (split.length != 2) {
                continue;
            }

            newList.addAll(getNumberRange(Integer.parseInt(split[0]), Integer.parseInt(split[1])));
        }

        return newList;
    }

    /**
     * Get a range of numbers as a list
     *
     * @param start The start of the range
     * @param end   The end of the range
     * @return A list of numbers
     */
    public static List<Integer> getNumberRange(int start, int end) {
        if (start == end) {
            return List.of(start);
        }

        final List<Integer> list = new ArrayList<>();
        for (int i = start; i <= end; i++) {
            list.add(i);
        }

        return list;
    }

    /**
     * Get a configuration value or default from the file config
     *
     * @param config The configuration file.
     * @param path   The path to the value
     * @param def    The default value if the original value doesnt exist
     * @return The config value or default value.
     */
    @SuppressWarnings("unchecked")
    private static <T> T get(CommentedFileConfiguration config, String path, T def) {
        return config.get(path) != null ? (T) config.get(path) : def;
    }

    /**
     * Get a value from a configuration section.
     *
     * @param section The configuration section
     * @param path    The path to the option.
     * @param def     The default value for the option.
     * @return The config option or the default.
     */
    @SuppressWarnings("unchecked")
    private static <T> T get(CommentedConfigurationSection section, String path, T def) {
        return section.get(path) != null ? (T) section.get(path) : def;
    }

}
