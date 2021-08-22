package net.skycraftia.skyhoppers.task;

import net.skycraftia.skyhoppers.SkyHoppersPlugin;
import net.skycraftia.skyhoppers.manager.DataManager;
import net.skycraftia.skyhoppers.obj.SkyHopper;
import org.bukkit.Material;
import org.bukkit.block.Furnace;
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

            final Inventory linkedInventory = hopper.getLinked().getInventory();

            hopperItems.forEach(itemStack -> {

                switch (hopper.getLinked().getInventory().getType()) {
                    // are we ready for a mess? Sure we are.
                    case BLAST_FURNACE, SMOKER, FURNACE -> this.transferToFurnace(itemStack, linkedInventory);
                    default -> this.transferToNormalInv(itemStack, linkedInventory, linkedInventory.getSize());
                }
            });

        });
    }

    /**
     * Transfer an item from a sky hopper to a linked furnace
     *
     * @param toTransfer The item being transferred to the furnace
     * @param furnaceInv The furnace inventory
     */
    private void transferToFurnace(ItemStack toTransfer, Inventory furnaceInv) {
        for (int i = 0; i < 2; i++) {
            int itemAmount = toTransfer.getAmount();
            if (itemAmount <= 0)
                return;

            final ItemStack containerItem = furnaceInv.getItem(i);

            if (toTransfer.getType().isFuel() && i == 0 || !toTransfer.getType().isFuel() && i == 1)
                continue;

            // slot is empty, fill it with the whole itemstack
            if (containerItem == null || containerItem.getType() == Material.AIR) {
                furnaceInv.setItem(i, toTransfer.clone());
                toTransfer.setAmount(0);
                return;
            } else if (toTransfer.isSimilar(containerItem)) {
                int amount = Math.min(containerItem.getMaxStackSize() - containerItem.getAmount(), toTransfer.getAmount());
                if (amount <= 0)
                    continue;

                containerItem.setAmount(containerItem.getAmount() + amount);
                if (itemAmount - amount <= 0)
                    toTransfer.setAmount(0);
                else
                    toTransfer.setAmount(toTransfer.getAmount() - amount);
                return;
            }
        }
    }

    /**
     * Transfer an item from a sky hopper to a linked dispenser or dropper
     *
     * @param toTransfer   The item being transferred to the dispenser or dropper
     * @param dispenserInv The dispenser or dropper inventory
     */
    private void transferToNormalInv(ItemStack toTransfer, Inventory dispenserInv, int slotCount) {
        for (int i = 0; i < slotCount; i++) {
            int itemAmount = toTransfer.getAmount();
            if (itemAmount <= 0)
                return;

            final ItemStack containerItem = dispenserInv.getItem(i);

            // slot is empty, fill it with the whole itemstack
            if (containerItem == null || containerItem.getType() == Material.AIR) {
                dispenserInv.setItem(i, toTransfer.clone());
                toTransfer.setAmount(0);

                return;
            } else if (toTransfer.isSimilar(containerItem)) {
                int amount = Math.min(containerItem.getMaxStackSize() - containerItem.getAmount(), toTransfer.getAmount());
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

// never uncomment this, It is such a bad idea
// It increases the server's overall cpu usage by 600% and increases ram usage just low enough to not crash
// your operating system.

// Yes I am keeping this in here.
//            hopperItems.forEach(itemStack -> {
//                final InventoryMoveItemEvent itemEvent = new InventoryMoveItemEvent(hopperInventory, itemStack, hopper.getLinked().getInventory(), true);
//                Bukkit.getPluginManager().callEvent(itemEvent);
//            });