package com.example.movie_nights_rest.controller;

import com.example.movie_nights_rest.command.auth.LoginRequest;
import com.example.movie_nights_rest.command.auth.LoginResponse;
import com.example.movie_nights_rest.command.user.CreateOrUpdateUserCommand;
import com.example.movie_nights_rest.command.user.UserResponseCommand;
import com.example.movie_nights_rest.config.security.jwt.TokenProvider;
import com.example.movie_nights_rest.model.AuthProvider;
import com.example.movie_nights_rest.model.Role;
import com.example.movie_nights_rest.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/auth")
@Api("Endpoints for authentication.")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    private final TokenProvider tokenProvider;

    public AuthController(AuthenticationManager authenticationManager, UserService userService, TokenProvider tokenProvider) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.tokenProvider = tokenProvider;
    }


    @PostMapping("/login")
    @ApiOperation("Login to your account")
    public LoginResponse login(
            @Valid
            @RequestBody
            @ApiParam("Login information")
                    LoginRequest request) {

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = tokenProvider.createToken(authentication);

        return LoginResponse.builder().token(token).build();
    }

    @PostMapping("/signup")
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
        return userService.create(request.getEmail(), request.getPassword(), roles, AuthProvider.local);
    }
}
