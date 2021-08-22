package net.skycraftia.skyhoppers.task;

import net.skycraftia.skyhoppers.obj.SkyHopper;
import net.skycraftia.skyhoppers.util.PluginUtils;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class HopperViewTask extends BukkitRunnable {

    private final Map<UUID, SkyHopper> hopperViewers = new HashMap<>();

    @Override
    public void run() {
        this.hopperViewers.forEach((uuid, hopper) -> {
            if (hopper.getLocation() == null)
                return;

            final Player player = Bukkit.getPlayer(uuid);
            if (player == null)
                return;

            // Show hopper outline
            final Location hopperCorner1 = PluginUtils.getBlockLoc(hopper.getLocation()).clone();
            final Location hopperCorner2 = hopperCorner1.clone().add(1, 1, 1);
            this.getHollowCube(hopperCorner1, hopperCorner2, 0.5).stream()
                    .filter(loc -> loc.getWorld() != null)
                    .forEach(location -> player.spawnParticle(Particle.REDSTONE, location.clone(), 1, 0.0, 0.0, 0.0, new Particle.DustOptions(Color.LIME, 1)));

            // Show Linked Container Outline
            if (hopper.getLinked() != null) {
                final Location corner1 = PluginUtils.getBlockLoc(hopper.getLinked().getLocation());
                final Location corner2 = corner1.clone().add(1, 1, 1);
                this.getHollowCube(corner1, corner2, 0.5).stream()
                        .filter(loc -> loc.getWorld() != null)
                        .forEach(location -> player.spawnParticle(Particle.REDSTONE, location.clone(), 1, 0.0, 0.0, 0.0, new Particle.DustOptions(Color.RED, 1)));
            }

            // Show Chunk Borders (Bedrock players cannot view chunk borders, This will help them visualize the borders of the chunk)
            Chunk chunk = hopper.getLocation().getChunk();
            final Location chunkCorner1 = new Location(chunk.getWorld(), chunk.getX() * 16, hopper.getLocation().getBlockY() - 3, chunk.getZ() * 16);
            final Location chunkCorner2 = chunkCorner1.clone().add(16, 8, 16);

            this.getHollowCube(chunkCorner1, chunkCorner2, 1).stream()
                    .filter(loc -> loc.getWorld() != null)
                    .forEach(loc -> player.spawnParticle(Particle.REDSTONE, loc.clone(), 1, 0.0, 0.0, 0.0, new Particle.DustOptions(Color.AQUA, 1)));
        });
    }


    /**
     * Get all the particle locations to spawn a hollow cube inbetween point A & Point B
     *
     * @param corner1          The first corner.
     * @param corner2          The second corner
     * @param particleDistance The distance between particles
     * @return The list of particle locations
     * @author Esophose
     * @ https://github.com/Rosewood-Development/PlayerParticles/blob/master/src/main/java/dev/esophose/playerparticles/styles/ParticleStyleOutline.java#L86
     */
    private List<Location> getHollowCube(Location corner1, Location corner2, double particleDistance) {
        List<Location> result = new ArrayList<>();
        World world = corner1.getWorld();
        double minX = Math.min(corner1.getX(), corner2.getX());
        double minY = Math.min(corner1.getY(), corner2.getY());
        double minZ = Math.min(corner1.getZ(), corner2.getZ());
        double maxX = Math.max(corner1.getX(), corner2.getX());
        double maxY = Math.max(corner1.getY(), corner2.getY());
        double maxZ = Math.max(corner1.getZ(), corner2.getZ());

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

    public Map<UUID, SkyHopper> getHopperViewers() {
        return hopperViewers;
    }

}
