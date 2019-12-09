package com.example.movie_nights_rest.model;

import com.example.movie_nights_rest.command.movie.OmdbMovieResponseCommand;
import com.fasterxml.jackson.annotation.JsonProperty;
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

    public Movie(OmdbMovieResponseCommand movie, String plot) {
        this.title = movie.getTitle();
        this.imdbID = movie.getImdbID();
        this.year = movie.getYear();
        this.rated = movie.getRated();
        this.released = movie.getReleased();
        this.runtime = movie.getRuntime();
        this.genre = movie.getGenre();
        this.director = movie.getDirector();
        this.writer = movie.getWriter();
        this.actors = movie.getActors();

        if (plot.equals("short")) this.shortPlot = movie.getPlot();
        else this.longPlot = movie.getPlot();

        this.language = movie.getLanguage();

        this.country = movie.getCountry();
        this.imdbRating = movie.getImdbRating();
        this.imdbVotes = movie.getImdbVotes();
        this.type = movie.getType();
        this.dvd = movie.getDvd();
        this.boxOffice = movie.getBoxOffice();
        this.production = movie.getProduction();
        this.website = movie.getWebsite();
        this.smallPoster = movie.getPoster();
        this.bigPoster = movie.getPoster().replace("X300.jpg", ".jpg");
    }

    private String title;

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

    private String smallPoster;

    private String bigPoster;
}
