package com.example.movie_nights_rest.command.user;

import com.example.movie_nights_rest.model.User;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;

@Data
@Builder
public class UserResponseCommand {
    private String id;
    private String username;
    private ArrayList<String> roles;

    public UserResponseCommand(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.roles = user.getRoles();
    }
}
