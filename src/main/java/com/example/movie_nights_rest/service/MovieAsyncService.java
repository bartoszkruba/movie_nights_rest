package com.example.movie_nights_rest.service;

import com.example.movie_nights_rest.command.movie.OmdbMovieResponseCommand;
import com.example.movie_nights_rest.exception.InternalServerErrorException;
import com.example.movie_nights_rest.exception.UnauthorizedException;
import com.example.movie_nights_rest.model.Movie;
import com.example.movie_nights_rest.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URISyntaxException;

@Service
@RequiredArgsConstructor
public class MovieAsyncService {
    private final String API_KEY = "57c2e939";

    private final MovieRepository movieRepository;

    @Async
    void saveMovieIfNotExist(String id) {
        if (movieRepository.findById(id).isEmpty()) {
            saveMovieToDatabase(id);
        }
    }

    @Async
    void saveMovieToDatabase(String id) {
        var uri = UriComponentsBuilder.newInstance()
                .scheme("http").host("www.omdbapi.com")
                .path("/")
                .queryParam("apikey", API_KEY);
        uri.queryParam("i", id);
        uri.buildAndExpand();

        OmdbMovieResponseCommand fetched;
        try {
            fetched = new RestTemplate().getForEntity(new URI(uri.toUriString()), OmdbMovieResponseCommand.class)
                    .getBody();
        } catch (URISyntaxException e) {
            throw new InternalServerErrorException();
        }

        if (fetched == null) throw new UnauthorizedException();
        Movie toSave = new Movie(fetched, "short");

        var fullPlotUri = UriComponentsBuilder.newInstance()
                .scheme("http").host("www.omdbapi.com")
                .path("/")
                .queryParam("apikey", API_KEY);
        fullPlotUri.queryParam("i", id);
        fullPlotUri.queryParam("plot", "full");
        fullPlotUri.buildAndExpand();

        try {
            fetched = new RestTemplate().getForEntity(new URI(fullPlotUri.toUriString()), OmdbMovieResponseCommand.class)
                    .getBody();
        } catch (URISyntaxException e) {
            throw new InternalServerErrorException();
        }

        toSave.setLongPlot(fetched.getPlot());

        movieRepository.save(toSave);
    }

}
