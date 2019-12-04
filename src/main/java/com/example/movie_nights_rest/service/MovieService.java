package com.example.movie_nights_rest.service;

import com.example.movie_nights_rest.command.movie.MovieResponseCommand;
import com.example.movie_nights_rest.model.Movie;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;

@Service
public class MovieService {

    public Flux<MovieResponseCommand> fetchMovies(String id, String title, String type, String year, String plot) {
        var uri = UriComponentsBuilder.newInstance()
                .scheme("http").host("www.omdbapi.com")
                .path("/")
                .queryParam("apikey", "57c2e939");

        if (id != null) uri.queryParam("i", id);
        if (title != null) uri.queryParam("t", title);
        if (type != null) uri.queryParam("type", type);
        if (year != null) uri.queryParam("y", year);
        if (plot != null) uri.queryParam("plot", plot);

        uri.buildAndExpand();

        return WebClient.create()
                .get()
                .uri(uri.toUriString())
                .retrieve()
                .bodyToFlux(Movie.class)
                .map(MovieResponseCommand::new);
    }
}
