package com.example.movie_nights_rest.controller;

import com.example.movie_nights_rest.command.user.CreateOrUpdateUserCommand;
import com.example.movie_nights_rest.command.user.UserResponseCommand;
import com.example.movie_nights_rest.model.Role;
import com.example.movie_nights_rest.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;


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


    @PostMapping
    @ApiOperation("Create new user. Available for all users.")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseCommand create(
            @Valid
            @RequestBody
            @ApiParam("User information.")
                    CreateOrUpdateUserCommand request
    ) {
        var roles = new ArrayList<String>();
        roles.add(Role.BASIC);
        return userService.create(request.getUsername(), request.getPassword(), roles);
    }
}
