package com.example.movie_nights_rest.controller;

import com.example.movie_nights_rest.command.movie.MoviePageResponseCommand;
import com.example.movie_nights_rest.command.movie.MovieResponseCommand;
import com.example.movie_nights_rest.model.Role;
import com.example.movie_nights_rest.model.Type;
import com.example.movie_nights_rest.service.MovieService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/movie")
@Api("Endpoints for movies")
@RequiredArgsConstructor
@CrossOrigin
public class MovieController {

    private final MovieService movieService;

    @GetMapping("/{id}")
    @ApiOperation("Find movie by id. Available for registered users.")
    @Secured({Role.ADMIN, Role.BASIC})
    public MovieResponseCommand fetchMovie(
            @ApiParam(value = "IMDB ID")
            @PathVariable
                    String id,
            @ApiParam(value = "Return short or full plot (short, full)")
            @RequestParam(defaultValue = "short")
                    String plot) {
        return movieService.fetchMovie(id, null, null, null, plot);
    }

    @GetMapping("/single")
    @ApiOperation("Fetch movies. Available for registered users.")
    @Secured({Role.ADMIN, Role.BASIC})
    public MovieResponseCommand fetchMovie(
            @ApiParam(value = "Movie title")
            @RequestParam
                    String title,
            @ApiParam(value = "Type of result to return (movie, series or episode)", defaultValue = "type")
            @RequestParam(required = false)
                    Type type,
            @ApiParam(value = "Year of release")
            @RequestParam(required = false)
                    String year,
            @ApiParam(value = "Return short or full plot (short, full)")
            @RequestParam(defaultValue = "short")
                    String plot
    ) {
        return movieService.fetchMovie(null, title, type, year, plot);
    }

    @GetMapping("/many")
    @ApiOperation("Fetch movie page. Available for registered users.")
    @Secured({Role.ADMIN, Role.BASIC})
    public MoviePageResponseCommand fetchMovies(
            @ApiParam(value = "Move title", required = true)
            @RequestParam(required = false)
                    String title,
            @ApiParam(value = "Type of result to return (movie, series or episode)", defaultValue = "movie")
            @RequestParam(required = false)
                    Type type,
            @ApiParam("Year of release")
            @RequestParam(required = false)
                    String year,
            @ApiParam(value = "Page number", defaultValue = "1", required = true)
            @RequestParam(defaultValue = "1")
                    Integer page
    ){
        return movieService.fetchMoviePage(title, type, year, page);
    }
}
