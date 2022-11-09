package xyz.oribuin.skyhoppers.manager;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.database.DataMigration;
import dev.rosewood.rosegarden.manager.AbstractDataManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import xyz.oribuin.skyhoppers.database.migration._1_CreateInitialTables;

import java.util.ArrayList;
import java.util.List;

public class DataManager extends AbstractDataManager {

    private final List<Location> hopperLocations = new ArrayList<>();

    public DataManager(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    /**
     * Load the data from the database
     */
    public void loadHoppers() {
        this.hopperLocations.clear();

        this.databaseConnector.connect(connection -> {
            final String query = "SELECT * FROM " + this.getTablePrefix() + "hoppers";
            try (var statement = connection.prepareStatement(query)) {
                var results = statement.executeQuery();
                while (results.next()) {
                    var x = results.getInt("x");
                    var y = results.getInt("y");
                    var z = results.getInt("z");
                    var world = results.getString("world");

                    this.hopperLocations.add(new Location(Bukkit.getWorld(world), x, y, z));
                }
            }

            this.rosePlugin.getLogger().info("Loaded " + this.hopperLocations.size() + " hoppers from the database.");
        });
    }

    /**
     * Add a hopper location to the database
     *
     * @param location The location to add
     */
    public void addHopper(Location location) {
        this.hopperLocations.add(location);

        this.async(() -> this.databaseConnector.connect(connection -> {
            final String query = "INSERT INTO " + this.getTablePrefix() + "hoppers (x, y, z, world) VALUES (?, ?, ?, ?)";
            try (var statement = connection.prepareStatement(query)) {
                statement.setInt(1, location.getBlockX());
                statement.setInt(2, location.getBlockY());
                statement.setInt(3, location.getBlockZ());
                statement.setString(4, location.getWorld().getName());
                statement.executeUpdate();
            }
        }));
    }

    /**
     * Remove a hopper location from the database
     *
     * @param location The location to remove
     */
    public void removeHopper(Location location) {
        this.hopperLocations.remove(location);

        this.async(() -> this.databaseConnector.connect(connection -> {
            final String query = "DELETE FROM " + this.getTablePrefix() + "hoppers WHERE x = ? AND y = ? AND z = ? AND world = ?";
            try (var statement = connection.prepareStatement(query)) {
                statement.setInt(1, location.getBlockX());
                statement.setInt(2, location.getBlockY());
                statement.setInt(3, location.getBlockZ());
                statement.setString(4, location.getWorld().getName());
                statement.executeUpdate();
            }
        }));
    }

    public List<Location> getHopperLocations() {
        return hopperLocations;
    }

    @Override
    public List<Class<? extends DataMigration>> getDataMigrations() {
        return List.of(_1_CreateInitialTables.class);
    }

    /**
     * Run a database task asynchronously
     *
     * @param runnable The task to run
     */
    public void async(Runnable runnable) {
        Bukkit.getScheduler().runTaskAsynchronously(this.rosePlugin, runnable);
    }

}
