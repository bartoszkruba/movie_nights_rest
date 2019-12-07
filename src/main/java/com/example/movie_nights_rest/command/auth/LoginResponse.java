package com.example.movie_nights_rest.command.auth;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("Login response")
@Builder
public class LoginResponse {

    @ApiModelProperty("JSON Web Token used for authentication")
    private String token;
}
