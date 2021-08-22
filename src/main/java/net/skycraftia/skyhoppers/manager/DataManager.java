package net.skycraftia.skyhoppers.manager;

import net.skycraftia.skyhoppers.SkyHoppers;
import net.skycraftia.skyhoppers.obj.CustomHopper;
import net.skycraftia.skyhoppers.util.PluginUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitTask;
import xyz.oribuin.orilibrary.database.DatabaseConnector;
import xyz.oribuin.orilibrary.database.MySQLConnector;
import xyz.oribuin.orilibrary.database.SQLiteConnector;
import xyz.oribuin.orilibrary.manager.Manager;
import xyz.oribuin.orilibrary.util.FileUtils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

public class DataManager extends Manager {

    private final SkyHoppers plugin = (SkyHoppers) this.getPlugin();
    private final Map<Location, CustomHopper> cachedHoppers = new HashMap<>();
    private DatabaseConnector connector = null;

    private final HopperManager hopperManager;

    public DataManager(SkyHoppers plugin) {
        super(plugin);
        this.hopperManager = this.plugin.getManager(HopperManager.class);
    }

    @Override
    public void enable() {

        final FileConfiguration config = this.plugin.getConfig();
        if (config.getBoolean("mysql.enabled")) {
            String hostName = config.getString("mysql.host");
            int port = config.getInt("mysql.port");
            String dbname = config.getString("mysql.dbname");
            String username = config.getString("mysql.username");
            String password = config.getString("mysql.password");
            boolean ssl = config.getBoolean("mysql.ssl");

            this.connector = new MySQLConnector(this.plugin, hostName, port, dbname, username, password, ssl);
        } else {
            FileUtils.createFile(this.plugin, "skyhoppers.db");
            this.connector = new SQLiteConnector(plugin, "skyhoppers.db");
        }

        this.async(task -> this.connector.connect(connection -> {
            final String createTable = "CREATE TABLE IF NOT EXISTS skyhoppers_hoppers (world TEXT, x DOUBLE, y DOUBLE, z DOUBLE, PRIMARY KEY (world, x, y, z))";
            connection.prepareStatement(createTable).executeUpdate();

            this.cachedHoppers.clear();
            try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM skyhoppers_hoppers")) {
                final ResultSet result = statement.executeQuery();

                while (result.next()) {
                    final World world = Bukkit.getWorld(result.getString("world"));
                    final double x = result.getDouble("x");
                    final double y = result.getDouble("y");
                    final double z = result.getDouble("z");

                    final Location loc = PluginUtils.getBlockLoc(new Location(world, x, y, z));

                    this.plugin.getServer().getScheduler().runTask(plugin, () -> {
                        final Optional<CustomHopper> customHopper = this.hopperManager.getHopperFromLocation(loc);
                        if (customHopper.isEmpty())
                            return;

                        this.cachedHoppers.put(loc, customHopper.get());
                    });

                }
            }
        }));

    }

    /**
     * Save the hopper into the database of the plugin.
     *
     * @param hopper The hopper that is being saved.
     */
    public void saveHopper(CustomHopper hopper) {

        if (hopper.getLocation() == null)
            return;

        final Location loc = PluginUtils.getBlockLoc(hopper.getLocation());
        this.cachedHoppers.put(loc, hopper);

        this.async(task -> this.connector.connect(connection -> {
            final String query = "REPLACE INTO skyhoppers_hoppers (world, x, y, z) VALUES (?, ?, ?, ?)";

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, Objects.requireNonNull(loc.getWorld()).getName());
                statement.setDouble(2, loc.getX());
                statement.setDouble(3, loc.getY());
                statement.setDouble(4, loc.getZ());
                statement.executeUpdate();
            }
        }));
    }

    /**
     * Delete a hopper from the plugin database.
     *
     * @param hopper The hopper to be deleted.
     */
    public void deleteHopper(CustomHopper hopper) {
        if (hopper.getLocation() == null)
            return;

        final Location loc = PluginUtils.getBlockLoc(hopper.getLocation());
        this.cachedHoppers.remove(loc);

        this.async(task -> this.connector.connect(connection -> {
            final String query = "DELETE FROM skyhoppers_hoppers WHERE world = ? AND x = ? AND y = ? AND z = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, Objects.requireNonNull(loc.getWorld()).getName());
                statement.setDouble(2, loc.getX());
                statement.setDouble(3, loc.getY());
                statement.setDouble(4, loc.getZ());
                statement.executeUpdate();
            }
        }));
    }

    @Override
    public void disable() {
        if (this.connector != null) {
            this.connector.closeConnection();
        }
    }

    public void async(Consumer<BukkitTask> callback) {
        this.plugin.getServer().getScheduler().runTaskAsynchronously(plugin, callback);
    }

    public Map<Location, CustomHopper> getCachedHoppers() {
        return cachedHoppers;
    }

}
