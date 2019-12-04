package com.example.movie_nights_rest.controller;

import com.example.movie_nights_rest.command.movie.MovieResponseCommand;
import com.example.movie_nights_rest.service.MovieService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/movie")
@Api("Endpoints for movies")
public class MovieController {

    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping
    @ApiOperation("Fetch movies. Available for registered users.")
    public Mono<MovieResponseCommand> fetchMovie(
            @ApiParam(value = "IMDB ID")
            @RequestParam(required = false, value = "i")
                    String id,
            @ApiParam(value = "Movie title")
            @RequestParam(required = false, value = "t")
                    String title,
            @ApiParam(value = "Type of result to return (movie, series or episode)")
            @RequestParam(required = false)
                    String type,
            @ApiParam(value = "Year of release")
            @RequestParam(required = false, value = "y")
                    String year,
            @ApiParam(value = "Return short or full plot (short, full)")
            @RequestParam(defaultValue = "short")
                    String plot
    ) {
        return movieService.fetchMovie(id, title, type, year, plot);
    }
}
