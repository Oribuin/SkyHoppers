package xyz.oribuin.skyhoppers;

import xyz.oribuin.orilibrary.OriPlugin;
import xyz.oribuin.skyhoppers.command.HopperCommand;
import xyz.oribuin.skyhoppers.listener.BlockListeners;
import xyz.oribuin.skyhoppers.listener.HopperListeners;
import xyz.oribuin.skyhoppers.listener.PlayerListeners;
import xyz.oribuin.skyhoppers.manager.DataManager;
import xyz.oribuin.skyhoppers.manager.HopperManager;
import xyz.oribuin.skyhoppers.manager.MessageManager;
import xyz.oribuin.skyhoppers.obj.SkyHopper;
import xyz.oribuin.skyhoppers.task.HopperViewTask;
import xyz.oribuin.skyhoppers.task.ItemTransferTask;
import xyz.oribuin.skyhoppers.task.SuctionTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SkyHoppersPlugin extends OriPlugin {

    private final Map<UUID, SkyHopper> linkingPlayers = new HashMap<>();
    private Map<UUID, SkyHopper> hopperViewers;

    @Override
    public void enablePlugin() {

        // Load Plugin Managers Asynchronously
        this.getManager(DataManager.class);
        this.getManager(HopperManager.class);
        this.getManager(MessageManager.class);

        // Register Plugin Listeners
        this.getServer().getPluginManager().registerEvents(new BlockListeners(this), this);
        this.getServer().getPluginManager().registerEvents(new HopperListeners(this), this);
        this.getServer().getPluginManager().registerEvents(new PlayerListeners(this), this);

        // Register Plugin Commands
        new HopperCommand(this);

        // Register Plugin Scheduled Tasks
        this.registerTasks();
    }

    public void registerTasks() {
        new SuctionTask(this).runTaskTimer(this, 0L, 10L);
        HopperViewTask hopperViewTask = new HopperViewTask(this);
        this.hopperViewers = hopperViewTask.getHopperViewers();
        hopperViewTask.runTaskTimerAsynchronously(this, 0, 5L);

        new ItemTransferTask(this).runTaskTimer(this, 0, this.getConfig().getLong("transfer-ticks"));
    }

    @Override
    public void disablePlugin() {

    }

    public Map<UUID, SkyHopper> getLinkingPlayers() {
        return linkingPlayers;
    }

    public Map<UUID, SkyHopper> getHopperViewers() {
        return hopperViewers;
    }

}
