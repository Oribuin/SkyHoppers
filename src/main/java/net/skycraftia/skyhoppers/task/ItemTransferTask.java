package net.skycraftia.skyhoppers.task;

import net.skycraftia.skyhoppers.SkyHoppers;
import net.skycraftia.skyhoppers.manager.DataManager;
import org.bukkit.scheduler.BukkitRunnable;

public class ItemTransferTask extends BukkitRunnable {

    private final DataManager data;

    public ItemTransferTask(final SkyHoppers plugin) {
        this.data = plugin.getManager(DataManager.class);
    }


    @Override
    public void run() {
        this.data.getCachedHoppers().forEach((location, hopper) -> {

        });

    }

}
