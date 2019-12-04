package com.example.movie_nights_rest.command.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("Create or update user command")
public class CreateOrUpdateUserCommand {

    @NotNull(message = "Username is required")
    @Pattern(regexp = "^(?![_.])(?!.*[_.]{2})[a-zA-Z0-9._]+(?<![_.])$", message = "Invalid username")
    @Size(min = 5, max = 25, message = "Invalid length")
    @ApiModelProperty("Username")
    String username;

    @NotNull(message = "Password is required")
    @Size(min = 5, message = "Password too short")
    @ApiModelProperty("Password")
    String password;
}
