package xyz.oribuin.skyhoppers.manager;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.manager.Manager;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import xyz.oribuin.skyhoppers.hook.protection.BentoBoxHook;
import xyz.oribuin.skyhoppers.hook.protection.IridiumSkyblockHook;
import xyz.oribuin.skyhoppers.hook.protection.LandsHook;
import xyz.oribuin.skyhoppers.hook.protection.ProtectionHook;
import xyz.oribuin.skyhoppers.hook.protection.TownyAdvancedHook;
import xyz.oribuin.skyhoppers.hook.protection.WorldGuardHook;
import xyz.oribuin.skyhoppers.hook.stacker.RoseStackerHook;
import xyz.oribuin.skyhoppers.hook.stacker.StackerHook;
import xyz.oribuin.skyhoppers.hook.stacker.WildStackerHook;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HookManager extends Manager {

    private final List<ProtectionHook> protectionHooks = new ArrayList<>(); // You could have more than one protection plugin installed.
    private StackerHook stackerHook = null; // In what world would you need more than one stacker plugin?

    public HookManager(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @Override
    public void reload() {
        final PluginManager pluginManager = this.rosePlugin.getServer().getPluginManager();

        final Map<String, Class<? extends ProtectionHook>> protectionPlugins = new LinkedHashMap<>() {{
            put("BentoBox", BentoBoxHook.class);
            put("IridiumSkyblock", IridiumSkyblockHook.class);
            put("Lands", LandsHook.class);
            put("Towny", TownyAdvancedHook.class);
            put("WorldGuard", WorldGuardHook.class);
        }};

        // Load the protection plugins
        for (Map.Entry<String, Class<? extends ProtectionHook>> entry : protectionPlugins.entrySet()) {
            if (pluginManager.isPluginEnabled(entry.getKey())) {
                try {
                    this.protectionHooks.add(entry.getValue().getConstructor().newInstance());
                } catch (Exception ignored) {
                    this.rosePlugin.getLogger().severe("Failed to load protection plugin " + entry.getKey());
                }
            }
        }

        final Map<String, Class<? extends StackerHook>> stackerPlugins = new HashMap<>() {{
            put("RoseStacker", RoseStackerHook.class);
            put("WildStacker", WildStackerHook.class);
        }};

        // Load the stacker plugin
        for (Map.Entry<String, Class<? extends StackerHook>> entry : stackerPlugins.entrySet()) {
            if (pluginManager.isPluginEnabled(entry.getKey())) {
                try {
                    this.stackerHook = entry.getValue().getConstructor().newInstance();
                } catch (Exception ignored) {
                    this.rosePlugin.getLogger().severe("Failed to load stacker plugin " + entry.getKey());
                }
            }
        }

    }

    /**
     * Check if a player can build at a location
     *
     * @param player   The player to check
     * @param location The location to check
     * @return true if the player can build
     */
    public boolean canBuild(Player player, Location location) {
        for (var hook : this.protectionHooks) {
            if (!hook.canBuild(player, location))
                return false;
        }

        return true;
    }

    /**
     * Check if a player can access a container at a location
     *
     * @param player   The player to check
     * @param location The location to check
     * @return true if the player can access the container
     */
    public boolean canOpen(Player player, Location location) {
        for (var hook : this.protectionHooks) {
            if (!hook.canOpen(player, location))
                return false;
        }

        return true;
    }

    @Override
    public void disable() {
        this.stackerHook = null;
        this.protectionHooks.clear();
    }

    public StackerHook getStackerHook() {
        return this.stackerHook;
    }

}
