package xyz.oribuin.skyhoppers.database.migration;

import dev.rosewood.rosegarden.database.DataMigration;
import dev.rosewood.rosegarden.database.DatabaseConnector;

import java.sql.Connection;
import java.sql.SQLException;

public class _1_CreateInitialTables extends DataMigration {

    public _1_CreateInitialTables() {
        super(1);
    }

    @Override
    public void migrate(DatabaseConnector connector, Connection connection, String tablePrefix) throws SQLException {

        final var query = "CREATE TABLE IF NOT EXISTS " + tablePrefix + "hoppers (x INT, y INT, z INT, world VARCHAR(50))";
        try (var statement = connection.prepareStatement(query)) {
            statement.executeUpdate();
        }

    }

}
