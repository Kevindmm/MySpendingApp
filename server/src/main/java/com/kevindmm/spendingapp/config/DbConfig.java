package com.kevindmm.spendingapp.config;

import javax.sql.DataSource;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.time.Clock;


@Configuration
@EnableJpaAuditing
public class DbConfig {
    @Value("${spendingapp.db.url:jdbc:sqlite:/app/data/mySpendingApp.db}")
    private String dbUrl;

    @Bean
    @Profile("!test")  // Only create this DataSource when NOT in test profile
    public DataSource dataSource() {
        final DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.sqlite.JDBC");
        dataSource.setUrl(dbUrl);
        return dataSource;
    }

    @Bean
    public Clock clock(){
        return Clock.systemDefaultZone();
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
