package com.myapp;

import com.zaxxer.hikari.HikariDataSource;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FingerprintDataSourceConfig {

    @Bean
    public DataSource dataSource(@Value("${fingerprint.db.path:}") String configuredPath) {
        Path databasePath = resolveDatabasePath(configuredPath);
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setDriverClassName("org.sqlite.JDBC");
        dataSource.setJdbcUrl("jdbc:sqlite:" + databasePath.toAbsolutePath().normalize());
        dataSource.setMaximumPoolSize(1);
        dataSource.setPoolName("fingerprint-sqlite-pool");
        return dataSource;
    }

    private Path resolveDatabasePath(String configuredPath) {
        List<Path> candidates = new ArrayList<Path>();
        if (hasText(configuredPath)) {
            candidates.add(Paths.get(configuredPath.trim()));
        }

        String userDir = System.getProperty("user.dir", ".");
        candidates.add(Paths.get(userDir, "src", "main", "resources", "file", "fingerprint-library.db"));
        candidates.add(Paths.get(userDir, "target", "classes", "file", "fingerprint-library.db"));
        candidates.add(Paths.get(userDir, "sensitive_path", "src", "main", "resources", "file", "fingerprint-library.db"));
        candidates.add(Paths.get(userDir, "sensitive_path", "target", "classes", "file", "fingerprint-library.db"));
        candidates.add(Paths.get("D:\\JavaVul\\sensitive_path\\src\\main\\resources\\file\\fingerprint-library.db"));

        for (Path candidate : candidates) {
            if (candidate != null && Files.exists(candidate)) {
                return candidate;
            }
        }

        throw new IllegalStateException("Fingerprint SQLite database not found. Checked: " + candidates);
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
