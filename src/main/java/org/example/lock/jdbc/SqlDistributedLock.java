package org.example.lock.jdbc;

import com.zaxxer.hikari.pool.HikariPool;

import java.sql.*;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;


public class SqlDistributedLock {
    private final HikariPool connectionPool;
    private final SqlTableInitializer sqlTableInitializer;
    private final SqlQueries sqlQueries;
    private final Clock clock;

    public SqlDistributedLock(
            HikariPool connectionPool, String tableName, Clock clock) {
        this.clock = clock;
        this.sqlQueries = new SqlQueries(tableName);
        this.connectionPool = connectionPool;
        this.sqlTableInitializer = new SqlTableInitializer(sqlQueries);
    }


    public void initialize() {
        try (Connection connection = connectionPool.getConnection()) {
            sqlTableInitializer.initialize(connection);
        } catch (Throwable e) {
            throw new RuntimeException("Could not initialize SQL table", e);
        }
    }


    public boolean acquire(LockRequest lockRequest) {
        Instant now = now();
        try (Connection connection = connectionPool.getConnection()) {
            return updateReleasedLock(connection, lockRequest, now)
                    || insertLock(connection, lockRequest, now);
        } catch (Throwable e) {
            throw new RuntimeException("Could not acquire lock: " + lockRequest, e);
        }
    }


    public boolean acquireOrProlong(LockRequest lockRequest) {
        Instant now = now();
        try (Connection connection = connectionPool.getConnection()) {
            return updateAcquiredOrReleasedLock(connection, lockRequest, now)
                    || insertLock(connection, lockRequest, now);
        } catch (Throwable e) {
            throw new RuntimeException("Could not acquire or prolong lock: " + lockRequest, e);
        }
    }


    public boolean forceAcquire(LockRequest lockRequest) {
        Instant now = now();
        try (Connection connection = connectionPool.getConnection()) {
            return updateLockById(connection, lockRequest, now)
                    || insertLock(connection, lockRequest, now);
        } catch (Throwable e) {
            throw new RuntimeException("Could not force acquire lock: " + lockRequest, e);
        }
    }

    private boolean updateReleasedLock(Connection connection, LockRequest lockRequest, Instant now) throws SQLException {
        String lockId = lockRequest.getLockId();
        Instant expiresAt = expiresAt(now, lockRequest.getDuration());
        try (PreparedStatement statement = getStatement(connection, sqlQueries.updateReleasedLock())) {
            statement.setString(1, lockRequest.getOwnerId());
            statement.setTimestamp(2, timestamp(now));
            setupOptionalTimestamp(statement, 3, expiresAt);
            statement.setString(4, lockId);
            statement.setTimestamp(5, timestamp(now));
            return statement.executeUpdate() > 0;
        }
    }

    private boolean updateAcquiredOrReleasedLock(Connection connection, LockRequest lockRequest, Instant now) throws SQLException {
        String lockId = lockRequest.getLockId();
        Instant expiresAt = expiresAt(now, lockRequest.getDuration());
        try (PreparedStatement statement = getStatement(connection, sqlQueries.updateAcquiredOrReleasedLock())) {
            statement.setString(1, lockRequest.getOwnerId());
            statement.setTimestamp(2, timestamp(now));
            setupOptionalTimestamp(statement, 3, expiresAt);
            statement.setString(4, lockId);
            statement.setString(5, lockRequest.getOwnerId());
            statement.setTimestamp(6, timestamp(now));
            return statement.executeUpdate() > 0;
        }
    }

    private boolean updateLockById(Connection connection, LockRequest lockRequest, Instant now) throws SQLException {
        String lockId = lockRequest.getLockId();
        Instant expiresAt = expiresAt(now, lockRequest.getDuration());
        try (PreparedStatement statement = getStatement(connection, sqlQueries.updateLockById())) {
            statement.setString(1, lockRequest.getOwnerId());
            statement.setTimestamp(2, timestamp(now));
            setupOptionalTimestamp(statement, 3, expiresAt);
            statement.setString(4, lockId);
            return statement.executeUpdate() > 0;
        }
    }

    private boolean insertLock(Connection connection, LockRequest lockRequest, Instant now) throws SQLException {
        String lockId = lockRequest.getLockId();
        Instant expiresAt = expiresAt(now, lockRequest.getDuration());
        try (PreparedStatement statement = getStatement(connection, sqlQueries.insertLock())) {
            statement.setString(1, lockId);
            statement.setString(2, lockRequest.getOwnerId());
            statement.setTimestamp(3, timestamp(now));
            setupOptionalTimestamp(statement, 4, expiresAt);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            if (!e.getMessage().toLowerCase().contains("duplicate")) {
                throw e;
            }
            return false;
        }
    }


    public boolean release(String lockId, String ownerId) {
        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement statement = getStatement(connection, sqlQueries.deleteAcquiredByIdAndOwnerId())
        ) {
            statement.setString(1, lockId);
            statement.setString(2, ownerId);
            statement.setTimestamp(3, timestamp(now()));
            return statement.executeUpdate() > 0;
        } catch (Throwable e) {
            throw new RuntimeException("Could not release lock: " + lockId + ", owner: " + ownerId, e);
        }
    }


    public boolean forceRelease(String lockId) {
        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement statement = getStatement(connection, sqlQueries.deleteAcquiredById())
        ) {
            statement.setString(1, lockId);
            statement.setTimestamp(2, timestamp(now()));
            return statement.executeUpdate() > 0;
        } catch (Throwable e) {
            throw new RuntimeException("Could not force release lock: " + lockId, e);
        }
    }


    public boolean forceReleaseAll() {
        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement statement = getStatement(connection, sqlQueries.deleteAll())
        ) {
            return statement.executeUpdate() > 0;
        } catch (Throwable e) {
            throw new IllegalStateException("Could not force release all locks", e);
        }
    }

    private Instant now() {
        return clock.instant();
    }

    private Instant expiresAt(Instant now, Duration duration) {
        if (duration == null || duration.isNegative()) {
            return null;
        }
        return now.plus(duration);
    }

    private void setupOptionalTimestamp(PreparedStatement statement, int index, Instant instant)
            throws SQLException {
        if (instant != null) {
            statement.setTimestamp(index, timestamp(instant));
        } else {
            statement.setNull(index, Types.TIMESTAMP);
        }
    }

    private Timestamp timestamp(Instant instant) {
        return new Timestamp(instant.toEpochMilli());
    }

    private PreparedStatement getStatement(Connection connection, String sql) {
        return sqlTableInitializer.getInitializedStatement(connection, sql);
    }
}