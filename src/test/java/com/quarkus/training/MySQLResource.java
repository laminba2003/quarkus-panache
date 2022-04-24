package com.quarkus.training;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.testcontainers.containers.MySQLContainer;
import java.util.Collections;
import java.util.Map;

public class MySQLResource implements QuarkusTestResourceLifecycleManager {

    static final MySQLContainer mysql = new MySQLContainer("mysql:5.7")
            .withDatabaseName("quarkus_training")
            .withUsername("user")
            .withPassword("password");

    @Override
    public Map<String, String> start() {
        mysql.start();
        return Collections.singletonMap(
                "quarkus.datasource.jdbc.url", mysql.getJdbcUrl()
        );
    }

    @Override
    public void stop() {
        mysql.stop();
    }
}

