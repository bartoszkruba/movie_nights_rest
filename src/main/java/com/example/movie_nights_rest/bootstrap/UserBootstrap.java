package com.example.movie_nights_rest.bootstrap;

import com.example.movie_nights_rest.model.AuthProvider;
import com.example.movie_nights_rest.model.Role;
import com.example.movie_nights_rest.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
@RequiredArgsConstructor
public class UserBootstrap implements CommandLineRunner {

    private final UserService userService;

    @Override
    public void run(String... args) throws Exception {
        var roles = new ArrayList<String>();
        roles.add(Role.ADMIN);
        userService.create("admin@email.com", "password", roles, AuthProvider.local);
    }
}
