package com.example.movie_nights_rest;

import com.example.movie_nights_rest.config.AppProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@EnableCaching
@EnableConfigurationProperties(AppProperties.class)
public class MovieNightsRestApplication {

    public static void main(String[] args) {
        SpringApplication.run(MovieNightsRestApplication.class, args);
    }
}
