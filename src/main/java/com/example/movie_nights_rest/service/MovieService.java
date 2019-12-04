package com.example.movie_nights_rest.service;

import com.example.movie_nights_rest.command.movie.MovieResponseCommand;
import com.example.movie_nights_rest.command.movie.OmdbMovieResponseCommand;
import com.example.movie_nights_rest.command.movie.OmdbSearchPageCommand;
import com.example.movie_nights_rest.model.Movie;
import com.example.movie_nights_rest.repository.MovieRepository;
import exception.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

@Service
public class MovieService {

    private final String API_KEY = "57c2e939";
    private final MovieRepository movieRepository;

    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    public Mono<MovieResponseCommand> fetchMovie(String id, String title, String type, String year, String plot) {
        if (id != null) return movieRepository.findById(id).map(movie -> new MovieResponseCommand(movie, plot))
                .switchIfEmpty(fetchFromOMDB(id, title, type, year, plot));
        else return fetchFromOMDB(id, title, type, year, plot);
    }

    private Mono<MovieResponseCommand> fetchMovie(String id) {
        return fetchMovie(id, null, null, null, null);
    }

    public Flux<MovieResponseCommand> fetchMoviePage(String title, String type, String year, Integer page) {
        var uri = UriComponentsBuilder.newInstance()
                .scheme("http").host("www.omdbapi.com")
                .path("/")
                .queryParam("apikey", API_KEY);
        if (title != null) uri.queryParam("s", title);
        if (type != null) uri.queryParam("type", type);
        if (year != null) uri.queryParam("y", year);
        if (page != null) uri.queryParam("page", page);

        uri.buildAndExpand();

        return WebClient.create()
                .get()
                .uri(uri.toUriString())
                .retrieve()
                .bodyToMono(OmdbSearchPageCommand.class)
                .map(OmdbSearchPageCommand::getSearch)
                .map(search -> search.stream()
                        .map(movie -> fetchMovie(movie.getImdbID()))
                        .collect(Collectors.toList()))
                .flatMapMany(Flux::mergeSequential);
    }

    private Mono<MovieResponseCommand> fetchFromOMDB(String id, String title, String type, String year, String plot) {

        var uri = UriComponentsBuilder.newInstance()
                .scheme("http").host("www.omdbapi.com")
                .path("/")
                .queryParam("apikey", API_KEY);

        if (id != null) uri.queryParam("i", id);
        else if (title != null) uri.queryParam("t", title);
        else throw new BadRequestException();

        if (type != null) uri.queryParam("type", type);
        if (year != null) uri.queryParam("y", year);
        if (plot != null) uri.queryParam("plot", plot);

        uri.buildAndExpand();

        return WebClient.create()
                .get()
                .uri(uri.toUriString())
                .retrieve()
                .bodyToMono(OmdbMovieResponseCommand.class)
                .map(movie -> {
                            saveMovieToDatabase(id).subscribe();
                            return new MovieResponseCommand(movie);
                        }
                );
    }

    private Mono<Void> saveMovieToDatabase(String id) {
        var uri = UriComponentsBuilder.newInstance()
                .scheme("http").host("www.omdbapi.com")
                .path("/")
                .queryParam("apikey", API_KEY);
        uri.queryParam("i", id);
        uri.buildAndExpand();

        return WebClient.create()
                .get()
                .uri(uri.toUriString())
                .retrieve()
                .bodyToMono(OmdbMovieResponseCommand.class)
                .map(movie -> {
                    var fullPlotUri = UriComponentsBuilder.newInstance()
                            .scheme("http").host("www.omdbapi.com")
                            .path("/")
                            .queryParam("apikey", API_KEY);
                    uri.queryParam("i", id);
                    uri.queryParam("plot", "full");
                    uri.buildAndExpand();
                    return WebClient.create().get().uri(fullPlotUri.toUriString()).retrieve()
                            .bodyToMono(OmdbMovieResponseCommand.class).map(fullPlotMovie -> {
                                var toSave = new Movie(movie, "short");
                                toSave.setLongPlot(fullPlotMovie.getPlot());
                                return movieRepository.save(toSave);
                            }).subscribe();
                }).then();
    }
}
