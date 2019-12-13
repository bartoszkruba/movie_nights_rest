package com.example.movie_nights_rest.command.movie;


import com.example.movie_nights_rest.model.Movie;
import com.example.movie_nights_rest.model.Rating;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel("Movie")
public class MovieResponseCommand {

    public MovieResponseCommand(OmdbMovieResponseCommand movie) {


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

        this.smallPoster = movie.getPoster();

        if (movie.getPoster() != null)
            this.bigPoster = movie.getPoster().replace("X300.jpg", ".jpg");
    }

    public MovieResponseCommand(Movie movie, String plot) {
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

        this.smallPoster = movie.getSmallPoster();
        this.bigPoster = movie.getBigPoster();
    }

    @ApiModelProperty("Movie title")
    private String title;

    @ApiModelProperty("IMDB ID of the movie")
    private String imdbID;

    @ApiModelProperty("Production year")
    private String year;

    @ApiModelProperty("Movie rating")
    private String rated;

    @ApiModelProperty("Link to low resolution poster")
    private String smallPoster;

    @ApiModelProperty("Link to large resolution poster")
    private String bigPoster;

    @ApiModelProperty("Release date")
    private String released;

    @ApiModelProperty("Movie length")
    private String runtime;

    @ApiModelProperty("Movie genre")
    private String genre;

    @ApiModelProperty("Movie Director")
    private String director;

    @ApiModelProperty("Movie writer")
    private String writer;

    @ApiModelProperty("Starring actors")
    private String actors;

    @ApiModelProperty("Movie plot")
    private String plot;

    @ApiModelProperty("Movie languages.")
    private String language;

    @ApiModelProperty("Production country")
    private String country;

    @Builder.Default
    @ApiModelProperty("List of rating from various sites")
    private ArrayList<Rating> ratings = new ArrayList<>();

    @ApiModelProperty("Movie metascore")
    private String metascore;

    @ApiModelProperty("IMDB rating of the movie")
    private String imdbRating;

    @ApiModelProperty("How many people rated the movie")
    private String imdbVotes;

    @ApiModelProperty("Series / Episode / Movie")
    private String type;

    @ApiModelProperty("DVD release date")
    private String dvd;

    @ApiModelProperty("Movie box office")
    private String boxOffice;

    @ApiModelProperty("Movie producer")
    private String production;

    @ApiModelProperty("Movie website")
    private String website;
}
