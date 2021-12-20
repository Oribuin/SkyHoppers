package xyz.oribuin.skyhoppers.task;

import xyz.oribuin.skyhoppers.SkyHoppersPlugin;
import xyz.oribuin.skyhoppers.manager.DataManager;
import xyz.oribuin.skyhoppers.obj.SkyHopper;
import org.bukkit.Material;
import org.bukkit.block.Furnace;
import org.bukkit.block.Hopper;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.oribuin.gui.Item;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ItemTransferTask extends BukkitRunnable {

    private final DataManager data;
    private int itemsPerTransfer = 2;

    public ItemTransferTask(final SkyHoppersPlugin plugin) {
        this.data = plugin.getManager(DataManager.class);
        if (plugin.getConfig().get("items-per-transfer") != null)
            this.itemsPerTransfer = plugin.getConfig().getInt("items-per-transfer");
    }

    @Override
    public void run() {
        this.data.getCachedHoppers().forEach((location, hopper) -> {
            if (hopper.getLinked() == null || hopper.getLocation() == null)
                return;

            if (!(hopper.getLocation().getBlock().getState() instanceof Hopper block))
                return;

            // Don't transfer items if the container is locked.
            if (block.isLocked())
                return;

            if (!SkyHopper.validContainers().contains(hopper.getLinked().getType()))
                return;

            final Inventory hopperInventory = block.getInventory();
            final List<ItemStack> hopperItems = Arrays.stream(hopperInventory.getContents())
                    .filter(Objects::nonNull)
                    .filter(itemStack -> itemStack.getType() != Material.AIR)
                    .collect(Collectors.toList());

            final Inventory linkedInventory = hopper.getLinked().getInventory();

            hopperItems.stream().findFirst().ifPresent(itemStack -> this.transferToNormalInv(itemStack, linkedInventory));
        });
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

            final ItemStack containerItem = inv.getItem(i);

            if (inv.getHolder() instanceof Furnace) {
                if (toTransfer.getType().isFuel() && i == 0 || !toTransfer.getType().isFuel() && i == 1)
                    continue;
            }

            int transferAmount = Math.min(itemAmount, itemsPerTransfer);

            // slot is empty, fill it with the whole itemstack
            if (containerItem == null || containerItem.getType() == Material.AIR) {
                inv.setItem(i, new Item.Builder(toTransfer.clone()).setAmount(transferAmount).create());
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

// never uncomment this, It is such a bad idea
// It increases the server's overall cpu usage by 600% and increases ram usage just low enough to not crash
// your operating system.

// Yes I am keeping this in here.
//            hopperItems.forEach(itemStack -> {
//                final InventoryMoveItemEvent itemEvent = new InventoryMoveItemEvent(hopperInventory, itemStack, hopper.getLinked().getInventory(), true);
//                Bukkit.getPluginManager().callEvent(itemEvent);
//            });