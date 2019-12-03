package com.example.movie_nights_rest.service;

import com.example.movie_nights_rest.command.auth.LoginRequest;
import com.example.movie_nights_rest.command.auth.LoginResponse;
import com.example.movie_nights_rest.command.user.UserResponseCommand;
import com.example.movie_nights_rest.model.Role;
import com.example.movie_nights_rest.model.User;
import com.example.movie_nights_rest.repository.UserRepository;
import com.example.movie_nights_rest.util.JwtTokenUtil;
import exception.UnauthorizedException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtTokenUtil jwtTokenUtil;

    public AuthService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder, JwtTokenUtil jwtTokenUtil) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    public Mono<LoginResponse> login(Mono<LoginRequest> request) {
        return request.flatMap(login -> userRepository.findByUsername(login.getUsername())
                .flatMap(user -> {
                    if (bCryptPasswordEncoder.matches(login.getPassword(), user.getPassword())) {
                        return Mono.just(new LoginResponse(jwtTokenUtil.generateToken(user)));
                    } else {
                        return Mono.error(new UnauthorizedException());
                    }
                }).switchIfEmpty(Mono.error(new UnauthorizedException())));
    }
}
