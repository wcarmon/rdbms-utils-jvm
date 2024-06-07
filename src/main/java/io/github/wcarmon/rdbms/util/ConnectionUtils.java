package io.github.wcarmon.rdbms.util;

import static java.util.Objects.requireNonNull;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Utility class for database connection.
 */
public final class ConnectionUtils {

    private ConnectionUtils() {
    }

    /**
     * Force connection test by executing a query.
     *
     * @param ds throws if cannot connect to the database
     */
    public static void testConnection(DataSource ds) {
        requireNonNull(ds, "ds is required and null.");

        testConnection(ds, "SELECT 1");
    }

    /**
     * Force connection test by executing a testQuery.
     *
     * @param ds        throws if cannot connect to the database
     * @param testQuery Database specific query to verify connectivity
     */
    public static void testConnection(DataSource ds, String testQuery) {
        requireNonNull(ds, "ds is required and null.");
        if (testQuery == null || testQuery.isBlank()) {
            throw new IllegalArgumentException("testQuery is required");
        }

        try (final Connection conn = ds.getConnection();
             final Statement stmt = conn.createStatement();
             final ResultSet res = stmt.executeQuery(testQuery)) {
            res.next();

        } catch (Exception ex) {
            throw new IllegalStateException("Failed on database connection test", ex);
        }
    }
}
