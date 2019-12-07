package com.example.movie_nights_rest.command.auth;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Data
@ApiModel("Login Request")
public class LoginRequest {

    @NotEmpty(message = "Username cannot be empty")
    @Email(message = "Invalid email")
    @ApiModelProperty("Email")
    private String email;

    @NotEmpty(message = "Password cannot be empty")
    @ApiModelProperty("Password")
    private String password;
}
