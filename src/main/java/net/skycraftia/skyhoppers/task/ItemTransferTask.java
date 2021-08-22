package net.skycraftia.skyhoppers.task;

import net.skycraftia.skyhoppers.SkyHoppersPlugin;
import net.skycraftia.skyhoppers.manager.DataManager;
import net.skycraftia.skyhoppers.obj.SkyHopper;
import org.bukkit.Material;
import org.bukkit.block.Hopper;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ItemTransferTask extends BukkitRunnable {

    private final DataManager data;

    public ItemTransferTask(final SkyHoppersPlugin plugin) {
        this.data = plugin.getManager(DataManager.class);
    }

    @Override
    public void run() {
        this.data.getCachedHoppers().forEach((location, hopper) -> {
            if (hopper.getLinked() == null || hopper.getLocation() == null)
                return;

            if (!(hopper.getLocation().getBlock().getState() instanceof Hopper block))
                return;

            if (!SkyHopper.validContainers().contains(hopper.getLinked().getType()))
                return;

            final Inventory hopperInventory = block.getInventory();
            final List<ItemStack> hopperItems = Arrays.stream(hopperInventory.getContents())
                    .filter(Objects::nonNull)
                    .filter(itemStack -> itemStack.getType() != Material.AIR)
                    .collect(Collectors.toList());

            // DO NOT DO THIS, THIS WILL COMPLETELY DESTROY A SERVER
            //            hopperItems.forEach(itemStack -> {
            //                final InventoryMoveItemEvent itemEvent = new InventoryMoveItemEvent(hopperInventory, itemStack, hopper.getLinked().getInventory(), true);
            //                Bukkit.getPluginManager().callEvent(itemEvent);
            //            });
        });

    }

}
