package com.example.movie_nights_rest.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document
@Builder
public class Movie {

    @Id
    private String imdbID;

    private String year;

    private String rated;

    private String released;

    private String runtime;

    private String genre;

    private String director;

    private String writer;

    private String actors;

    private String longPlot;

    private String shortPlot;

    private String language;

    private String country;

    @Builder.Default
    private ArrayList<Rating> ratings = new ArrayList<>();

    private String metascore;

    private String imdbRating;

    private String imdbVotes;

    private String type;

    private String dvd;

    private String boxOffice;

    private String production;

    private String website;
}
