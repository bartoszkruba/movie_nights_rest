package com.example.movie_nights_rest.controller;

import com.example.movie_nights_rest.command.auth.LoginRequest;
import com.example.movie_nights_rest.command.user.UserResponseCommand;
import com.example.movie_nights_rest.model.Role;
import com.example.movie_nights_rest.service.UserService;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;


@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @PreAuthorize("hasRole('" + Role.ADMIN + "')")
    public Flux<UserResponseCommand> getAll() {
        return userService.getAll();
    }

    @PostMapping
    public Mono<UserResponseCommand> create(@RequestBody Mono<LoginRequest> request) {
        return request.flatMap(r -> {
            var roles = new ArrayList<String>();
            roles.add(Role.BASIC);
            return userService.create(r.getUsername(), r.getPassword(), roles);
        }).switchIfEmpty(Mono.empty());
    }
}
