package com.example.movie_nights_rest.bootstrap;

import com.example.movie_nights_rest.model.Role;
import com.example.movie_nights_rest.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class UserBootstrap implements CommandLineRunner {

    private final UserService userService;

    public UserBootstrap(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void run(String... args) throws Exception {
        var roles = new ArrayList<String>();
        roles.add(Role.ADMIN);
        userService.create("admin", "password", roles);
    }
}
