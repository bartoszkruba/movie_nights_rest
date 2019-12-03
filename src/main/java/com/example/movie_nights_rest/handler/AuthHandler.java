package com.example.movie_nights_rest.handler;

import com.example.movie_nights_rest.command.auth.LoginRequest;
import com.example.movie_nights_rest.command.auth.LoginResponse;
import com.example.movie_nights_rest.model.User;
import com.example.movie_nights_rest.repository.UserRepository;
import com.example.movie_nights_rest.util.JwtTokenUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Component
public class AuthHandler {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserRepository userRepository;
    private final JwtTokenUtil jwtTokenUtil;

    public AuthHandler(BCryptPasswordEncoder bCryptPasswordEncoder, UserRepository userRepository,
                       JwtTokenUtil jwtTokenUtil) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.userRepository = userRepository;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    public Mono login(ServerRequest request) {
        Mono<LoginRequest> loginRequest = request.bodyToMono(LoginRequest.class);

        return loginRequest.flatMap(login -> userRepository.findByUsername(login.getUsername())
                .flatMap(user -> {
                    if (bCryptPasswordEncoder.matches(login.getPassword(), user.getPassword())) {
                        return ServerResponse.ok()
                                .contentType(APPLICATION_JSON)
                                .body(BodyInserters.fromValue(new LoginResponse(jwtTokenUtil.generateToken(user))));
                    } else {
                        return ServerResponse.badRequest().build();
                    }
                }).switchIfEmpty(ServerResponse.badRequest().build()));
    }

    public Mono signup(ServerRequest request) {
        Mono<User> userMono = request.bodyToMono(User.class);
        return userMono.map(user -> {
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
            ArrayList<String> roles = new ArrayList<>();
            roles.add("ROLE_BASIC");
            user.setRoles(roles);
            return user;
        }).flatMap(user -> userRepository.findByUsername(user.getUsername())
                .flatMap(dbUser -> ServerResponse.badRequest().build())
                .switchIfEmpty(userRepository.save(user).flatMap(savedUser -> ServerResponse.ok()
                        .body(BodyInserters.fromValue(savedUser)))));
    }
}
