package com.example.movie_nights_rest.command.auth;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}
