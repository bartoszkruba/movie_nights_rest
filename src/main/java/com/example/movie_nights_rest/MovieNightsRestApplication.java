package com.example.movie_nights_rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class MovieNightsRestApplication {

    public static void main(String[] args) {
        SpringApplication.run(MovieNightsRestApplication.class, args);
    }

}
