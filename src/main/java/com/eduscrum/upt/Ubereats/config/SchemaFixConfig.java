package com.eduscrum.upt.Ubereats.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Configuration class for database schema fixes.
 * Runs at startup to apply necessary schema modifications.
 *
 * @version 0.5.0 (2025-11-05)
 */
@Configuration
public class SchemaFixConfig {

    @Bean
    public CommandLineRunner fixTeamTableSchema(JdbcTemplate jdbcTemplate) {
        return args -> {
            try {
                // Relax the constraint on the legacy 'project_id' column to allow NULLs
                // This fixes the 500/403 error during Team creation
                jdbcTemplate.execute("ALTER TABLE teams MODIFY COLUMN project_id BIGINT NULL");
                System.out.println("✅ SUCCESS: Fixed 'teams' table schema (project_id is now nullable).");
            } catch (Exception e) {
                // It might fail if the column doesn't exist or is already fixed, which is fine.
                System.out.println("ℹ️ NOTE: Attempted to fix 'teams' schema: " + e.getMessage());
            }
        };
    }
}
