package com.example.movie_nights_rest.router;

import com.example.movie_nights_rest.handler.AuthHandler;
import com.example.movie_nights_rest.handler.UserHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

@Configuration
public class Router {

    private final AuthHandler authHandler;
    private final UserHandler userHandler;

    public Router(AuthHandler authHandler, UserHandler userHandler) {
        this.authHandler = authHandler;
        this.userHandler = userHandler;
    }

    @Bean
    public RouterFunction userRoutes() {
        return RouterFunctions
                .route(GET("/users").and(accept(MediaType.APPLICATION_JSON)), userHandler::listUsers);
    }

    @Bean
    public RouterFunction authRoutes() {
        return RouterFunctions
                .route(POST("/auth/login").and(accept(MediaType.APPLICATION_JSON)), authHandler::login)
                .andRoute(POST("/auth/signup").and(accept(MediaType.APPLICATION_JSON)), authHandler::signup);
    }
}
