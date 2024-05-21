package com.nparmenov.reservation_system;

import javax.sql.DataSource;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@ComponentScan
public class AppConfig {

    @Bean
    @SuppressWarnings("rawtypes")
    public DataSource provideH2DataSource() {
        DataSourceBuilder dsb = DataSourceBuilder.create();

        dsb.url("jdbc:h2:mem:testdb");
        dsb.driverClassName("org.h2.Driver");
        dsb.username("sa");
        dsb.password("password");

        return dsb.build();
    }

    @Bean
    public JdbcTemplate provideH2JdbcTemplate() {
        return new JdbcTemplate(provideH2DataSource());
    }

    @Bean
    public WebClient provideWebClient() {
        return WebClient.builder()
        .baseUrl("http://localhost:8080")
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .build();
    }
}
