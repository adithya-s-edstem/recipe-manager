package com.recipemanager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;

/**
 * Base class for integration tests: the full application context with MockMvc, backed by a real
 * Postgres 16. The container is started once per JVM and shared by every subclass, and Spring
 * caches the context across subclasses that don't add their own configuration.
 */
@SpringBootTest
@AutoConfigureMockMvc
public abstract class IntegrationTest {

    @ServiceConnection
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine").withReuse(true);

    static {
        POSTGRES.start();
    }

    @Autowired
    protected MockMvc mockMvc;
}
