/*
package org.example;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.javacrumbs.shedlock.core.DefaultLockingTaskExecutor;
import net.javacrumbs.shedlock.core.LockConfiguration;
import net.javacrumbs.shedlock.core.LockingTaskExecutor;
import net.javacrumbs.shedlock.provider.jdbc.JdbcLockProvider;

import javax.sql.DataSource;
import java.time.Duration;
import java.time.Instant;

public class Test {
    public static void main(String[] args) {

        JdbcLockProvider jdbcLockProvider = new JdbcLockProvider(connectionPool());
        LockingTaskExecutor executor = new DefaultLockingTaskExecutor(jdbcLockProvider);
        Instant lockAtMostUntil = Instant.now().plusSeconds(600);
        executor.executeWithLock(new Runnable() {
            @Override
            public void run() {

            }
        }, new LockConfiguration( Instant.now(),"lockName", Duration.ofSeconds(1),Duration.ofSeconds(1)));
    }

    private static DataSource connectionPool() {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName("org.postgresql.Driver");
        config.setJdbcUrl("jdbc:postgresql://localhost:5432/postgres");
        config.setUsername("postgres");
        config.setPassword("vinod123");
        return new HikariDataSource(config);
    }
}
*/
