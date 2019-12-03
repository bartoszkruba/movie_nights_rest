package com.example.movie_nights_rest.handler;

import com.example.movie_nights_rest.model.User;
import com.example.movie_nights_rest.repository.UserRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class UserHandler {

    private final UserRepository userRepository;

    public UserHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Mono listUsers(ServerRequest request) {
        return ServerResponse.ok().body(userRepository.findAll(), User.class);
    }

}
