package xyz.oribuin.skyhoppers.task;

import dev.rosewood.rosestacker.api.RoseStackerAPI;
import dev.rosewood.rosestacker.stack.StackedItem;
import xyz.oribuin.skyhoppers.SkyHoppersPlugin;
import xyz.oribuin.skyhoppers.manager.DataManager;
import xyz.oribuin.skyhoppers.obj.FilterType;
import xyz.oribuin.skyhoppers.obj.SkyHopper;
import xyz.oribuin.skyhoppers.util.PluginUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Hopper;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SuctionTask extends BukkitRunnable {

    private final DataManager data;

    public SuctionTask(final SkyHoppersPlugin plugin) {
        this.data = plugin.getManager(DataManager.class);
    }

    @Override
    public void run() {
        this.data.getCachedHoppers().values().stream()
                .filter(SkyHopper::isEnabled)
                .filter(hopper -> hopper.getLocation() != null)
                .forEach(hopper -> {
                    final Block block = hopper.getLocation().getBlock();
                    if (!(block.getState() instanceof Hopper container))
                        return;

                    // Don't suction items if the container is locked.
                    if (container.isLocked())
                        return;

                    final Chunk chunk = hopper.getLocation().getChunk();
                    final List<Item> chunkItems = Arrays.stream(chunk.getEntities())
                            .filter(entity -> entity instanceof Item)
                            .map(entity -> (Item) entity)
                            .collect(Collectors.toList());

                    // @author Esophose
                    chunkItems.forEach(item -> {

                        if (hopper.getFilterType() == FilterType.DESTROY && PluginUtils.itemFiltered(item.getItemStack(), hopper)) {
                            item.getWorld().spawnParticle(Particle.SMOKE_NORMAL, item.getLocation(), 3, 0.0, 0.0, 0.0, 0.0);
                            item.remove();
                            return;
                        }

                        if (PluginUtils.itemFiltered(item.getItemStack(), hopper))
                            return;

                        int itemAmount = this.getItemAmount(item);

                        for (int i = 0; i < 5; i++) {
                            if (itemAmount <= 0)
                                return;

                            final ItemStack hopperItem = container.getInventory().getItem(i);
                            if (hopperItem == null || hopperItem.getType() == Material.AIR) {
                                // slot is empty, fill it with as many items as we can
                                for (int x = 0; x < 5; x++)
                                    block.getWorld().spawnParticle(Particle.REDSTONE, PluginUtils.centerLocation(block.getLocation()), 5, 0.3, 0.3, 0.3, 0.0, new Particle.DustOptions(Color.fromRGB(255, 192, 203), 1));

                                // Create a copy of the item and set the amount to at most the max stack size of the material
                                ItemStack copy = item.getItemStack().clone();
                                copy.setAmount(Math.min(itemAmount, copy.getMaxStackSize()));
                                itemAmount -= copy.getAmount();

                                item.getWorld().spawnParticle(Particle.SPELL_WITCH, item.getLocation(), 3, 0.0, 0.0, 0.0, 0.0);
                                container.getInventory().setItem(i, item.getItemStack());

                                if (itemAmount <= 0)
                                    item.remove();

                                continue;
                            }

                            if (item.getItemStack().isSimilar(hopperItem)) {
                                // slot has the exact same itemstack in it, can we increase the stack size any more?
                                int amount = Math.min(hopperItem.getMaxStackSize() - hopperItem.getAmount(), itemAmount);
                                if (amount > 0) {
                                    // we sure can! add as much as we can from the chunk item to the existing hopper item
                                    hopperItem.setAmount(hopperItem.getAmount() + amount);
                                    if (itemAmount - amount <= 0) { // are we removing *all* the items?
                                        item.remove();
                                    }

                                    itemAmount -= amount;

                                    // ooo! pretty!
                                    item.getWorld().spawnParticle(Particle.SPELL_WITCH, item.getLocation(), 3, 0.0, 0.0, 0.0, 0.0);

                                    for (int x = 0; x < 5; x++)
                                        block.getWorld().spawnParticle(Particle.REDSTONE, PluginUtils.centerLocation(block.getLocation()), 5, 0.3, 0.3, 0.3, 0.0, new Particle.DustOptions(Color.fromRGB(255, 192, 203), 1));
                                }
                            }
                        }

                        this.setItemAmount(item, itemAmount);
                    });
                });
    }

    public int getItemAmount(Item item) {
        if (Bukkit.getPluginManager().isPluginEnabled("RoseStacker")) {
            StackedItem stackedItem = RoseStackerAPI.getInstance().getStackedItem(item);
            if (stackedItem != null)
                return stackedItem.getStackSize();
        }

        return item.getItemStack().getAmount();
    }

    public void setItemAmount(Item item, int amount) {
        if (Bukkit.getPluginManager().isPluginEnabled("RoseStacker")) {
            StackedItem stackedItem = RoseStackerAPI.getInstance().getStackedItem(item);
            if (stackedItem != null) {
                stackedItem.setStackSize(amount);
                return;
            }
        }

        item.getItemStack().setAmount(amount);
    }

}