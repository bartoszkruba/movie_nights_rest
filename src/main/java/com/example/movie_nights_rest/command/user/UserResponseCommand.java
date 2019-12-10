package com.example.movie_nights_rest.command.user;

import com.example.movie_nights_rest.model.User;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel("User response")
public class UserResponseCommand {

    @ApiModelProperty("User ID")
    private String id;
    @ApiModelProperty("Username")
    private String username;
    @ApiModelProperty("User roles")
    private ArrayList<String> roles;

    public UserResponseCommand(User user) {
        this.id = user.getId();
        this.username = user.getName();
        this.roles = user.getRoles();
    }
}
