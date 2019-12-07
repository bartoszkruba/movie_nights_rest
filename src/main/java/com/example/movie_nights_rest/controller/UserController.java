package com.example.movie_nights_rest.controller;

import com.example.movie_nights_rest.annotation.CurrentUser;
import com.example.movie_nights_rest.command.user.UserResponseCommand;
import com.example.movie_nights_rest.config.security.UserPrincipal;
import com.example.movie_nights_rest.model.Role;
import com.example.movie_nights_rest.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/user")
@Api("Endpoints for users.")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @PreAuthorize("hasRole('" + Role.ADMIN + "')")
    @ApiOperation("Get all users. Available for ADMIN users.")
    public Iterable<UserResponseCommand> getAll() {
        return userService.getAll();
    }


    @GetMapping
    @Secured({Role.BASIC, Role.ADMIN})
    @ApiOperation("Get information about your account")
    public UserResponseCommand getCurrentUser(@CurrentUser UserPrincipal userPrincipal) {
        return userService.getById(userPrincipal.getId());
    }
}
