package net.skycraftia.skyhoppers;

import net.skycraftia.skyhoppers.command.HopperCommand;
import net.skycraftia.skyhoppers.listener.BlockListeners;
import net.skycraftia.skyhoppers.listener.HopperListeners;
import net.skycraftia.skyhoppers.listener.PlayerListeners;
import net.skycraftia.skyhoppers.manager.DataManager;
import net.skycraftia.skyhoppers.manager.HopperManager;
import net.skycraftia.skyhoppers.manager.MessageManager;
import net.skycraftia.skyhoppers.obj.SkyHopper;
import net.skycraftia.skyhoppers.task.HopperViewTask;
import net.skycraftia.skyhoppers.task.ItemTransferTask;
import net.skycraftia.skyhoppers.task.SuctionTask;
import xyz.oribuin.orilibrary.OriPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SkyHoppersPlugin extends OriPlugin {

    private final Map<UUID, SkyHopper> linkingPlayers = new HashMap<>();
    private Map<UUID, SkyHopper> hopperViewers;

    @Override
    public void enablePlugin() {

        // Load Plugin Managers Asynchronously
        this.getServer().getScheduler().runTaskAsynchronously(this, () -> {
            this.getManager(DataManager.class);
            this.getManager(HopperManager.class);
            this.getManager(MessageManager.class);
        });

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
        new SuctionTask(this).runTaskTimer(this, 0L, 15L);
        HopperViewTask hopperViewTask = new HopperViewTask();
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
