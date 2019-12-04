package com.example.movie_nights_rest.command.movie;


import com.example.movie_nights_rest.model.Movie;
import com.example.movie_nights_rest.model.Rating;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MovieResponseCommand {

    public MovieResponseCommand(OmdbMovieResponseCommand movie) {

        this.imdbID = movie.getImdbID();
        this.year = movie.getYear();
        this.rated = movie.getRated();
        this.released = movie.getReleased();
        this.runtime = movie.getRuntime();
        this.genre = movie.getGenre();
        this.director = movie.getDirector();
        this.writer = movie.getWriter();
        this.actors = movie.getActors();
        this.plot = movie.getPlot();
        this.language = movie.getLanguage();
        this.country = movie.getCountry();
        this.imdbRating = movie.getImdbRating();
        this.imdbVotes = movie.getImdbVotes();
        this.type = movie.getType();
        this.dvd = movie.getDvd();
        this.boxOffice = movie.getBoxOffice();
        this.production = movie.getProduction();
        this.website = movie.getWebsite();
    }

    public MovieResponseCommand(Movie movie, String plot) {

        this.imdbID = movie.getImdbID();
        this.year = movie.getYear();
        this.rated = movie.getRated();
        this.released = movie.getReleased();
        this.runtime = movie.getRuntime();
        this.genre = movie.getGenre();
        this.director = movie.getDirector();
        this.writer = movie.getWriter();
        this.actors = movie.getActors();

        if (plot != null && plot.equals("short")) this.plot = movie.getShortPlot();
        else if (plot != null) this.plot = movie.getLongPlot();

        this.language = movie.getLanguage();
        this.country = movie.getCountry();
        this.imdbRating = movie.getImdbRating();
        this.imdbVotes = movie.getImdbVotes();
        this.type = movie.getType();
        this.dvd = movie.getDvd();
        this.boxOffice = movie.getBoxOffice();
        this.production = movie.getProduction();
        this.website = movie.getWebsite();
    }

    private String imdbID;

    private String year;

    private String rated;

    private String released;

    private String runtime;

    private String genre;

    private String director;

    private String writer;

    private String actors;

    private String plot;

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
