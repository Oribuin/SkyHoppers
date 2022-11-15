package xyz.oribuin.skyhoppers.hook;

import dev.rosewood.rosegarden.RosePlugin;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class PAPI extends PlaceholderExpansion {

    private final RosePlugin plugin;
    private static boolean enabled = false;

    public PAPI(final RosePlugin plugin) {
        this.plugin = plugin;

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            enabled = true;
            this.register();
        }

    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        // TODO
        return null;
    }

    /**
     * Apply PAPI placeholders to a string
     *
     * @param player The player to apply the placeholders to
     * @param text   The text to apply the placeholders to
     * @return The text with the placeholders applied
     */
    public static String apply(OfflinePlayer player, String text) {
        if (enabled) {
            return PlaceholderAPI.setPlaceholders(player, text);
        }

        return text;
    }

    @Override
    public @NotNull String getIdentifier() {
        return this.plugin.getDescription().getName();
    }

    @Override
    public @NotNull String getAuthor() {
        return this.plugin.getDescription().getAuthors().get(0);
    }

    @Override
    public @NotNull String getVersion() {
        return this.plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return enabled;
    }

    public static boolean isEnabled() {
        return enabled;
    }

}
