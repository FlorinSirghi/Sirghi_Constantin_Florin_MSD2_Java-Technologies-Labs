package com.example.Lab2.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.*;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Configuration
@EnableConfigurationProperties(DatabaseProps.class)
public class DataSourceConfig {

    @Bean
    @Profile("dev")
    @ConditionalOnExpression("'${db.useH2:true}' == 'true'")
    @Qualifier("h2DataSource")
    public DataSource h2DataSource(DatabaseProps p) {
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setDriverClassName("org.h2.Driver");
        ds.setUrl("jdbc:h2:mem:" + (p.getName() != null ? p.getName() : "devdb") + ";DB_CLOSE_DELAY=-1");
        ds.setUsername(p.getUser() != null ? p.getUser() : "sa");
        ds.setPassword(p.getPassword() != null ? p.getPassword() : "");
        return ds;
    }

    @Bean
    @Profile({"dev","prod"})
    @ConditionalOnExpression("'${db.useH2:true}' == 'false'")
    @Qualifier("postgresDataSource")
    public DataSource postgresDataSource(DatabaseProps p) {
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setDriverClassName("org.postgresql.Driver");
        String url = String.format("jdbc:postgresql://%s:%d/%s", p.getHost(), p.getPort(), p.getName());
        ds.setUrl(url);
        ds.setUsername(p.getUser());
        ds.setPassword(p.getPassword());
        return ds;
    }

    // Let Spring inject whichever DataSource bean matched the conditions
    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
