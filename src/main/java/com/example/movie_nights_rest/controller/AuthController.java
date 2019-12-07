package com.example.movie_nights_rest.controller;

import com.example.movie_nights_rest.command.auth.LoginRequest;
import com.example.movie_nights_rest.command.auth.LoginResponse;
import com.example.movie_nights_rest.service.AuthService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@Api("Endpoints for authentication.")
public class AuthController {

//    private final AuthService authService;
//
//    public AuthController(AuthService authService) {
//        this.authService = authService;
//    }
//
//    @PostMapping("/login")
//    @ApiOperation("Login to your account")
//    public LoginResponse login(
//            @Valid
//            @RequestBody
//            @ApiParam("Login information")
//                    LoginRequest request) {
//        return authService.login(request);
//    }
}
