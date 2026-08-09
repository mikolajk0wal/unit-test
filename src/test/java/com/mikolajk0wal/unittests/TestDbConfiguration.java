package com.mikolajk0wal.unittests;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testcontainers.postgresql.PostgreSQLContainer;

import javax.sql.DataSource;

@TestConfiguration(proxyBeanMethods = false)
class TestDbConfiguration {

    @Bean
    @ServiceConnection
    PostgreSQLContainer postgreSQLContainer() {
        return new PostgreSQLContainer("postgres:16-alpine");
    }

    @Bean
    JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    DataSource dataSource(PostgreSQLContainer postgres) {
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.username(postgres.getUsername());
        dataSourceBuilder.password(postgres.getPassword());
        dataSourceBuilder.driverClassName(postgres.getDriverClassName());
        dataSourceBuilder.url(postgres.getJdbcUrl());
        return dataSourceBuilder.build();
    }
}