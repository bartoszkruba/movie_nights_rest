package com.example.movie_nights_rest.model;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;

@Document
@Data
public class User {
    private String id;
    private String username;
    private String password;
    private ArrayList<String> roles;
}
