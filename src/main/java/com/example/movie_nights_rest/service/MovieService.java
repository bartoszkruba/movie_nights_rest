package com.example.movie_nights_rest.service;

import com.example.movie_nights_rest.command.movie.MoviePageResponseCommand;
import com.example.movie_nights_rest.command.movie.MovieResponseCommand;
import com.example.movie_nights_rest.command.movie.OmdbMovieResponseCommand;
import com.example.movie_nights_rest.command.movie.OmdbSearchPageCommand;
import com.example.movie_nights_rest.exception.UnauthorizedException;
import com.example.movie_nights_rest.model.Movie;
import com.example.movie_nights_rest.repository.MovieRepository;
import com.example.movie_nights_rest.exception.BadRequestException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;

@Service
public class MovieService {

    private final String API_KEY = "57c2e939";
    private final MovieRepository movieRepository;

    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    public MovieResponseCommand fetchMovie(String id, String title, String type, String year, String plot) {

        var local = movieRepository.findById(id);

        return local.map(movie -> new MovieResponseCommand(movie, plot))
                .orElseGet(() -> {
                    try {
                        return fetchFromOMDB(id, title, type, year, plot);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                        return null;
                    }
                });
    }

    private MovieResponseCommand fetchMovie(String id) {
        return fetchMovie(id, null, null, null, null);
    }

    public MoviePageResponseCommand fetchMoviePage(String title, String type, String year, Integer page)
            throws URISyntaxException {
        var uri = UriComponentsBuilder.newInstance()
                .scheme("http").host("www.omdbapi.com")
                .path("/")
                .queryParam("apikey", API_KEY);
        if (title != null) uri.queryParam("s", URLDecoder.decode(title));
        if (type != null) uri.queryParam("type", type);
        if (year != null) uri.queryParam("y", year);
        if (page != null) uri.queryParam("page", page);

        uri.buildAndExpand();

        var response = new RestTemplate().getForEntity(new URI(uri.toUriString()), OmdbSearchPageCommand.class).getBody();
        if (response == null) throw new BadRequestException();
        var movies = response.getSearch().stream().map(movie -> fetchMovie(movie.getImdbID()));

        var moviePage = new MoviePageResponseCommand();
        moviePage.setTotalMovies(response.getTotalResults());
        movies.forEach(movie -> moviePage.getMovies().add(movie));

        return moviePage;
    }

    private MovieResponseCommand fetchFromOMDB(String id, String title, String type, String year, String plot)
            throws URISyntaxException {
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

        var movie = new RestTemplate().getForEntity(uri.toUriString(), OmdbMovieResponseCommand.class).getBody();

        if (movie == null) return null;
        saveMovieIfNotExist(id);

        return new MovieResponseCommand(movie);
    }

    @Async
    void saveMovieIfNotExist(String id) throws URISyntaxException {
        if (movieRepository.findById(id).isEmpty()) saveMovieToDatabase(id);

    }

    private void saveMovieToDatabase(String id) throws URISyntaxException {
        var uri = UriComponentsBuilder.newInstance()
                .scheme("http").host("www.omdbapi.com")
                .path("/")
                .queryParam("apikey", API_KEY);
        uri.queryParam("i", id);
        uri.buildAndExpand();

        var fetched = new RestTemplate().getForEntity(new URI(uri.toUriString()), OmdbMovieResponseCommand.class)
                .getBody();

        if (fetched == null) throw new UnauthorizedException();
        Movie toSave = new Movie(fetched, "short");

        var fullPlotUri = UriComponentsBuilder.newInstance()
                .scheme("http").host("www.omdbapi.com")
                .path("/")
                .queryParam("apikey", API_KEY);
        uri.queryParam("i", id);
        uri.queryParam("plot", "full");
        uri.buildAndExpand();

        fetched = new RestTemplate().getForEntity(new URI(fullPlotUri.toUriString()), OmdbMovieResponseCommand.class)
                .getBody();

        toSave.setLongPlot(fetched.getPlot());

        movieRepository.save(toSave);
    }
}
