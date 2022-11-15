package xyz.oribuin.skyhoppers;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.manager.Manager;
import dev.rosewood.rosegarden.utils.NMSUtil;
import org.bukkit.Bukkit;
import xyz.oribuin.skyhoppers.hook.PAPI;
import xyz.oribuin.skyhoppers.hopper.SkyHopper;
import xyz.oribuin.skyhoppers.listener.BlockListeners;
import xyz.oribuin.skyhoppers.listener.HopperListeners;
import xyz.oribuin.skyhoppers.listener.PlayerListeners;
import xyz.oribuin.skyhoppers.manager.CommandManager;
import xyz.oribuin.skyhoppers.manager.ConfigurationManager;
import xyz.oribuin.skyhoppers.manager.ConfigurationManager.Settings;
import xyz.oribuin.skyhoppers.manager.DataManager;
import xyz.oribuin.skyhoppers.manager.HookManager;
import xyz.oribuin.skyhoppers.manager.HopperManager;
import xyz.oribuin.skyhoppers.manager.LocaleManager;
import xyz.oribuin.skyhoppers.manager.MenuManager;
import xyz.oribuin.skyhoppers.task.HopperViewTask;
import xyz.oribuin.skyhoppers.task.ItemTransferTask;
import xyz.oribuin.skyhoppers.task.SuctionTask;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SkyHoppersPlugin extends RosePlugin {

    private final Map<UUID, SkyHopper> linkingPlayers = new HashMap<>();

    private static SkyHoppersPlugin instance;

    public static SkyHoppersPlugin getInstance() {
        return instance;
    }

    public SkyHoppersPlugin() {
        super(-1, -1, ConfigurationManager.class, DataManager.class, LocaleManager.class, CommandManager.class);
        instance = this;
    }

    @Override
    protected void enable() {

        if (NMSUtil.getVersionNumber() < 17) {
            this.getLogger().severe("This plugin requires 1.17 or higher, Disabling plugin!");
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Register Listeners
        final var pluginManager = this.getServer().getPluginManager();
        pluginManager.registerEvents(new BlockListeners(this), this);
        pluginManager.registerEvents(new PlayerListeners(this), this);
        pluginManager.registerEvents(new HopperListeners(this), this);

        // Register PlaceholderAPI
        new PAPI(this);
    }


    @Override
    public void reload() {
        super.reload();

        // Register Scheduled Tasks
        new SuctionTask(this).runTaskTimer(this, 0L, Settings.TASKS_SUCTION.getLong()); // Sucks items into hoppers
        new ItemTransferTask(this).runTaskTimer(this, 0L, Settings.TASKS_TRANSFER.getLong()); // Transfers items between hoppers
        new HopperViewTask(this).runTaskTimerAsynchronously(this, 0L, Settings.TASKS_VISUALISER.getLong()); // Shows funny particles
    }

    @Override
    protected void disable() {
        Bukkit.getScheduler().cancelTasks(this);
    }

    @Override
    protected List<Class<? extends Manager>> getManagerLoadPriority() {
        return List.of(HopperManager.class, HookManager.class, MenuManager.class);
    }

    public Map<UUID, SkyHopper> getLinkingPlayers() {
        return linkingPlayers;
    }

}
