package com.example.movie_nights_rest.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

@Document
@NoArgsConstructor
@AllArgsConstructor
@Data
public class MovieWatching {
    private LocalDateTime startTime;

    private LocalDateTime endTime;

    @DBRef(lazy = true)
    private ArrayList<User> attendees;

    @DBRef(lazy = true)
    private Movie movie;
}
