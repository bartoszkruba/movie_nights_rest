package com.example.movie_nights_rest.command.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("Create or update user command")
public class CreateOrUpdateUserCommand {

    @Email(message = "Invalid email")
    @NotNull(message = "Email is required")
    @ApiParam("Email")
    String email;

    @NotNull(message = "Password is required")
    @Size(min = 5, message = "Password too short")
    @ApiModelProperty("Password")
    String password;
}
