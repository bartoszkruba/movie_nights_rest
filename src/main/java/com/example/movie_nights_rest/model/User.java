package com.example.movie_nights_rest.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;

@Document
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    private String id;

    @Indexed(unique = true)
    private String name;

    @Indexed(unique = true)
    private String email;
    private AuthProvider provider;
    private String providerId;

    @DBRef(lazy = true)
    @EqualsAndHashCode.Exclude
    @Builder.Default
    private ArrayList<User> friends = new ArrayList<>();

    @Builder.Default
    @EqualsAndHashCode.Exclude
    private ArrayList<String> roles = new ArrayList<>();
}
