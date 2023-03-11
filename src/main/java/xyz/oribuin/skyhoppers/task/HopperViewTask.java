package xyz.oribuin.skyhoppers.task;

import dev.rosewood.rosegarden.RosePlugin;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.oribuin.skyhoppers.manager.ConfigurationManager.Settings;
import xyz.oribuin.skyhoppers.manager.HopperManager;
import xyz.oribuin.skyhoppers.util.PluginUtils;

import java.util.ArrayList;
import java.util.List;

import static xyz.oribuin.skyhoppers.util.PluginUtils.centerLocation;

public class HopperViewTask extends BukkitRunnable {

    private final HopperManager manager;
    private final double suctionRange;

    public HopperViewTask(final RosePlugin plugin) {
        this.manager = plugin.getManager(HopperManager.class);
        this.suctionRange = Settings.SUCTION_RANGE.getDouble();
    }

    @Override
    public void run() {

        for (var entry : this.manager.getHopperViewers().entrySet()) {
            // We get this again so the visualizer will update if changed.
            var skyHopper = this.manager.getHopperFromCache(entry.getValue().getLocation());
            if (entry.getKey() == null || skyHopper == null)
                return;

            final var player = this.manager.getCachedPlayer(entry.getKey());
            if (player == null)
                return;

            final var hopperLocation = skyHopper.getLocation();

            if (hopperLocation == null || !hopperLocation.getWorld().equals(player.getWorld())) {
                return;
            }

            // Show hopper outline
            final var hopperCorner1 = PluginUtils.getBlockLoc(skyHopper.getLocation()).clone();
            final var hopperCorner2 = hopperCorner1.clone().add(1, 1, 1);
            this.getHollowCube(hopperCorner1, hopperCorner2, 0.5).stream()
                    .filter(loc -> loc.getWorld() != null)
                    .forEach(location -> player.spawnParticle(Particle.REDSTONE, location.clone(), 1, 0.0, 0.0, 0.0, new Particle.DustOptions(Color.LIME, 1)));

            if (skyHopper.isEnabled()) {
                final var centerLocation = centerLocation(skyHopper.getLocation()).clone();
                centerLocation.subtract(0.0, 0.5, 0.0);

                for (int i = 0; i < 5; i++)
                    player.spawnParticle(Particle.REDSTONE, centerLocation, 2, suctionRange / 5, 0.0, suctionRange / 5, 0.0, new Particle.DustOptions(Color.PURPLE, 1));
            }

            // Show Linked Container Outline
            if (skyHopper.getLinked() != null) {
                final var corner1 = PluginUtils.getBlockLoc(skyHopper.getLinked().getLocation());
                final var corner2 = corner1.clone().add(1, 1, 1);
                this.getHollowCube(corner1, corner2, 0.5).stream()
                        .filter(loc -> loc.getWorld() != null)
                        .forEach(location -> player.spawnParticle(Particle.REDSTONE, location.clone(), 1, 0.0, 0.0, 0.0, new Particle.DustOptions(Color.RED, 1)));
            }

            // Show Chunk Borders (Bedrock players cannot view chunk borders, This will help them visualize the borders of the chunk)
            var min = skyHopper.getLocation().clone().subtract(suctionRange / 2.0, suctionRange / 2.0, suctionRange / 2.0);
            min = centerLocation(min);
            var max = skyHopper.getLocation().clone().add(suctionRange / 2.0, suctionRange / 2.0, suctionRange / 2.0);
            max = centerLocation(max);


            this.getHollowCube(min, max, 1).stream()
                    .filter(loc -> loc.getWorld() != null)
                    .forEach(loc -> player.spawnParticle(Particle.REDSTONE, loc.clone(), 1, 0.0, 0.0, 0.0, new Particle.DustOptions(Color.AQUA, 1)));

        }

    }


    /**
     * Get all the particle locations to spawn a hollow cube in between point A & Point B
     *
     * @param corner1          The first corner.
     * @param corner2          The second corner
     * @param particleDistance The distance between particles
     * @return The list of particle locations
     * @author Esophose
     * @ <a href="https://github.com/Rosewood-Development/PlayerParticles/blob/master/src/main/java/dev/esophose/playerparticles/styles/ParticleStyleOutline.java#L86">...</a>
     */
    private List<Location> getHollowCube(Location corner1, Location corner2, double particleDistance) {
        List<Location> result = new ArrayList<>();
        var world = corner1.getWorld();
        var minX = Math.min(corner1.getX(), corner2.getX());
        var minY = Math.min(corner1.getY(), corner2.getY());
        var minZ = Math.min(corner1.getZ(), corner2.getZ());
        var maxX = Math.max(corner1.getX(), corner2.getX());
        var maxY = Math.max(corner1.getY(), corner2.getY());
        var maxZ = Math.max(corner1.getZ(), corner2.getZ());

        for (double x = minX; x <= maxX; x += particleDistance) {
            result.add(new Location(world, x, minY, minZ));
            result.add(new Location(world, x, maxY, minZ));
            result.add(new Location(world, x, minY, maxZ));
            result.add(new Location(world, x, maxY, maxZ));
        }

        for (double y = minY; y <= maxY; y += particleDistance) {
            result.add(new Location(world, minX, y, minZ));
            result.add(new Location(world, maxX, y, minZ));
            result.add(new Location(world, minX, y, maxZ));
            result.add(new Location(world, maxX, y, maxZ));
        }

        for (double z = minZ; z <= maxZ; z += particleDistance) {
            result.add(new Location(world, minX, minY, z));
            result.add(new Location(world, maxX, minY, z));
            result.add(new Location(world, minX, maxY, z));
            result.add(new Location(world, maxX, maxY, z));
        }

        return result;
    }

}
