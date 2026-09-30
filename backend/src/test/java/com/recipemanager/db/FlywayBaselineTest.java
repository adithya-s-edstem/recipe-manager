package com.recipemanager.db;

import static org.assertj.core.api.Assertions.assertThat;

import com.recipemanager.IntegrationTest;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

class FlywayBaselineTest extends IntegrationTest {

    @Autowired
    Flyway flyway;

    @Autowired
    JdbcTemplate jdbc;

    @Test
    @DisplayName("Migrations apply cleanly: V1 baseline is applied and enables pg_trgm")
    void baselineApplied() {
        MigrationInfo[] applied = flyway.info().applied();
        assertThat(applied)
                .anySatisfy(m -> {
                    assertThat(m.getVersion().getVersion()).isEqualTo("1");
                    assertThat(m.getState().isApplied()).isTrue();
                    assertThat(m.getState().isFailed()).isFalse();
                });
        assertThat(flyway.info().pending()).isEmpty();

        Integer trgm = jdbc.queryForObject(
                "SELECT count(*) FROM pg_extension WHERE extname = 'pg_trgm'", Integer.class);
        assertThat(trgm).isEqualTo(1);
    }
}
