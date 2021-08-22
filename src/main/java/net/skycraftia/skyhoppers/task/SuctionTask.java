package net.skycraftia.skyhoppers.task;

import net.skycraftia.skyhoppers.SkyHoppersPlugin;
import net.skycraftia.skyhoppers.manager.DataManager;
import net.skycraftia.skyhoppers.obj.SkyHopper;
import net.skycraftia.skyhoppers.obj.FilterType;
import net.skycraftia.skyhoppers.util.PluginUtils;
import org.bukkit.Chunk;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;
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
    //    private final boolean roseStackerEnabled;

    public SuctionTask(final SkyHoppersPlugin plugin) {
        this.data = plugin.getManager(DataManager.class);
        //        this.roseStackerEnabled = Bukkit.getPluginManager().isPluginEnabled("RoseStacker");
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

                        // TODO, Add RoseStacker Support.
                        for (int i = 0; i < 5; i++) {
                            int itemAmount = item.getItemStack().getAmount();

                            if (itemAmount <= 0)
                                return;

                            final ItemStack hopperItem = container.getInventory().getItem(i);
                            if (hopperItem == null || hopperItem.getType() == Material.AIR) {
                                // slot is empty, fill it with the whole itemstack
                                for (int x = 0; x < 5; x++)
                                    block.getWorld().spawnParticle(Particle.REDSTONE, PluginUtils.centerLocation(block.getLocation()), 5, 0.3, 0.3, 0.3, 0.0, new Particle.DustOptions(Color.fromRGB(255, 192, 203), 1));

                                item.getWorld().spawnParticle(Particle.SPELL_WITCH, item.getLocation(), 3, 0.0, 0.0, 0.0, 0.0);
                                container.getInventory().setItem(i, item.getItemStack());
                                item.remove();

                                return;
                            } else if (item.getItemStack().isSimilar(hopperItem)) {
                                // slot has the exact same itemstack in it, can we increase the stack size any more?
                                int amount = Math.min(hopperItem.getMaxStackSize() - hopperItem.getAmount(), item.getItemStack().getAmount());

                                if (amount > 0) {
                                    // we sure can! add as much as we can from the chunk item to the existing hopper item
                                    hopperItem.setAmount(hopperItem.getAmount() + amount);
                                    if (itemAmount - amount <= 0) { // are we removing *all* the items?
                                        item.remove();
                                    } else {
                                        item.getItemStack().setAmount(item.getItemStack().getAmount() - amount);
                                    }

                                    // ooo! pretty!
                                    item.getWorld().spawnParticle(Particle.SPELL_WITCH, item.getLocation(), 3, 0.0, 0.0, 0.0, 0.0);

                                    for (int x = 0; x < 5; x++)
                                        block.getWorld().spawnParticle(Particle.REDSTONE, PluginUtils.centerLocation(block.getLocation()), 5, 0.3, 0.3, 0.3, 0.0, new Particle.DustOptions(Color.fromRGB(255, 192, 203), 1));
                                    return;
                                }
                            }
                        }
                    });
                });
    }


}