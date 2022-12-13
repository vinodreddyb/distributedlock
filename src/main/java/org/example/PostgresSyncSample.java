/*
package org.example;

import com.coditory.sherlock.DistributedLock;
import com.coditory.sherlock.Sherlock;
import com.coditory.sherlock.SherlockMigrator;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.javacrumbs.shedlock.core.DefaultLockingTaskExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.time.Clock;
import java.time.Duration;

import static com.coditory.sherlock.SqlSherlockBuilder.sqlSherlock;

public class PostgresSyncSample {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    public void assignOccurrenceSequences() {
        DefaultLockingTaskExecutor()
        final List<String> cities = buildingDao.retrievePendingCities();
//https://binarycoders.dev/2021/02/01/postgresql-advisory-locks/
        for (final String city : cities) {
            final int lockId = Math.abs(Hashing.sha256().newHasher()
                    .putString(city, StandardCharsets.UTF_8)
                    .hash().asInt());

            logger.info("Taking advisory_lock {} for city {} ", lockId, city);
            try (Connection connection = dataSource.getConnection()) {
                connection.setAutoCommit(true);

                final boolean lockObtained;
                try (Statement statement = connection.createStatement()) {
                    lockObtained = statement.execute(format("select pg_try_advisory_lock(%d)", lockId));
                }

                if (lockObtained) {
                    try {
                        final int updates = buildingDao.populateOccurrenceSequences(city);
                        logger.info("Assigning {} sequences for city {}", updates, city);
                    } finally {
                        try (Statement statement = connection.createStatement()) {
                            statement.execute(format("select pg_advisory_unlock(%d)", lockId));
                        }

                        logger.info("Released advisory_lock {} for city {}", lockId, city);
                    }
                } else {
                    logger.info("advisory_lock {} for city {} already taken", lockId, city);
                }
            } catch (SQLException e) {
                throw new IllegalStateException(e);
            }
        }
    }
    private final Sherlock sherlock = sqlSherlock()
            .withClock(Clock.systemDefaultZone())
            .withLockDuration(Duration.ofMinutes(5))
            .withUniqueOwnerId()
            .withConnection(connectionPool().getConnection())
            .withLocksTable("LOCKS")
            .build();

    public PostgresSyncSample() throws SQLException {
    }

    private static DataSource connectionPool() {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName("org.postgresql.Driver");
        config.setJdbcUrl("jdbc:postgresql://localhost:5432/postgres");
        config.setUsername("postgres");
        config.setPassword("vinod123");
        return new HikariDataSource(config);
    }

    void samplePostgresLockUsage() throws Exception {
        logger.info(">>> SAMPLE: Lock usage");
        DistributedLock lock = sherlock.createLock("sample-lock");
        lock.acquireAndExecute(() -> logger.info("Lock acquired!"));
    }

    private void samplePostgresMigration() {
        logger.info(">>> SAMPLE: Migration");
        // first commit - all migrations are executed
        new SherlockMigrator("db-migration", sherlock)
                .addChangeSet("change set 1", () -> logger.info(">>> Change set 1"))
                .addChangeSet("change set 2", () -> logger.info(">>> Change set 2"))
                .migrate();
        // second commit - only new change set is executed
        new SherlockMigrator("db-migration", sherlock)
                .addChangeSet("change set 1", () -> logger.info(">>> Change set 1"))
                .addChangeSet("change set 2", () -> logger.info(">>> Change set 2"))
                .addChangeSet("change set 3", () -> logger.info(">>> Change set 3"))
                .migrate();
    }

    void runSamples() throws Exception {
        samplePostgresLockUsage();
        samplePostgresMigration();
    }

    public static void main(String[] args) throws Exception {
        new PostgresSyncSample().runSamples();
    }
}*/
