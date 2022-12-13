package org.example.lock.jdbc;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.pool.HikariPool;

import javax.sql.DataSource;
import java.time.Clock;
import java.time.Duration;

public class DBLockClient {
    private static SqlDistributedLock sqlDistributedLockConnector = null;

    static  {
        sqlDistributedLockConnector = new SqlDistributedLock(connectionPool(),
                "msgLock", Clock.systemUTC());
        sqlDistributedLockConnector.initialize();
    }

    public static boolean lock (String lockId, String ownerId, Duration duration) {
        return sqlDistributedLockConnector.acquire(new LockRequest(lockId,ownerId,duration));
    }

    public static void releaseLock(String lockId, String ownerId) {
         sqlDistributedLockConnector.release(lockId,ownerId);
    }

    public static HikariPool connectionPool() {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName("org.postgresql.Driver");
        config.setJdbcUrl("jdbc:postgresql://localhost:5432/postgres");
        config.setUsername("postgres");
        config.setPassword("vinod123");
        return new HikariPool(config);

    }
}
