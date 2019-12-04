package com.example.movie_nights_rest.command.auth;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
@ApiModel("Login Request")
public class LoginRequest {

    @NotEmpty(message = "Username cannot be empty")
    @ApiModelProperty("Username")
    private String username;

    @NotEmpty(message = "Password cannot be empty")
    @ApiModelProperty("Password")
    private String password;
}
