package xyz.oribuin.skyhoppers.task;

import dev.rosewood.rosegarden.RosePlugin;
import org.bukkit.Material;
import org.bukkit.block.Furnace;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.oribuin.skyhoppers.hopper.SkyHopper;
import xyz.oribuin.skyhoppers.manager.ConfigurationManager.Settings;
import xyz.oribuin.skyhoppers.manager.HopperManager;
import xyz.oribuin.skyhoppers.util.ItemBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ItemTransferTask extends BukkitRunnable {

    private final HopperManager manager;
    private final int itemsPerTransfer;

    public ItemTransferTask(final RosePlugin plugin) {
        this.manager = plugin.getManager(HopperManager.class);
        this.itemsPerTransfer = Settings.TASKS_ITEMS_PER_TRANSFER.getInt();
    }

    @Override
    public void run() {

        for (SkyHopper skyHopper : this.manager.getEnabledHoppers()) {
            if (skyHopper == null || skyHopper.getLocation() == null || skyHopper.getLinked() == null)
                return;

            if (!(skyHopper.getLocation().getBlock().getState() instanceof org.bukkit.block.Hopper hopperBlock))
                return;

            if (hopperBlock.isLocked())
                return;

            final Inventory hopperInventory = hopperBlock.getInventory();
            final Inventory linkedInventory = skyHopper.getLinked().getInventory();

            final List<ItemStack> hopperItems = Arrays.stream(hopperInventory.getContents())
                    .filter(Objects::nonNull)
                    .filter(item -> item.getType() != Material.AIR)
                    .toList();


            hopperItems.stream().findFirst().ifPresent(itemStack -> this.transferToNormalInv(itemStack, linkedInventory));
        }

    }

    /**
     * Transfer an item from a sky hopper to a linked dispenser or dropper
     *
     * @param toTransfer The item being transferred to the dispenser or dropper
     * @param inv        The inventory.
     */
    private void transferToNormalInv(ItemStack toTransfer, Inventory inv) {
        for (int i = 0; i < inv.getSize(); i++) {
            int itemAmount = toTransfer.getAmount();
            if (itemAmount <= 0)
                return;

            var containerItem = inv.getItem(i);

            if (inv.getHolder() instanceof Furnace) {
                if (toTransfer.getType().isFuel() && i == 0 || !toTransfer.getType().isFuel() && i == 1)
                    continue;
            }

            var transferAmount = Math.min(itemAmount, itemsPerTransfer);

            // slot is empty, fill it with the whole itemstack
            if (containerItem == null || containerItem.getType() == Material.AIR) {
                inv.setItem(i, new ItemBuilder(toTransfer.clone()).setAmount(transferAmount).create());
                toTransfer.setAmount(toTransfer.getAmount() - transferAmount);

                return;
            } else if (toTransfer.isSimilar(containerItem)) {
                int amount = Math.min(containerItem.getMaxStackSize() - containerItem.getAmount(), transferAmount);
                if (amount <= 0)
                    continue;

                if (itemAmount - amount <= 0)
                    toTransfer.setAmount(0);
                else
                    toTransfer.setAmount(toTransfer.getAmount() - amount);

                containerItem.setAmount(containerItem.getAmount() + amount);
                return;
            }

        }

    }

}