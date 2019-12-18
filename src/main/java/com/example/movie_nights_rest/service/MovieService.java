package com.example.movie_nights_rest.service;

import com.example.movie_nights_rest.command.movie.MoviePageResponseCommand;
import com.example.movie_nights_rest.command.movie.MovieResponseCommand;
import com.example.movie_nights_rest.command.movie.OmdbMovieResponseCommand;
import com.example.movie_nights_rest.command.movie.OmdbSearchPageCommand;
import com.example.movie_nights_rest.exception.NoContentException;
import com.example.movie_nights_rest.model.Type;
import com.example.movie_nights_rest.exception.InternalServerErrorException;
import com.example.movie_nights_rest.repository.MovieRepository;
import com.example.movie_nights_rest.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;

@Service
@RequiredArgsConstructor
public class MovieService {

    @Value("${omdbkey}")
    private String API_KEY;
    private final MovieRepository movieRepository;
    private final MovieAsyncService movieAsyncService;

    public MovieResponseCommand fetchMovie(String id, String title, Type type, String year, String plot) {

        if (StringUtils.isEmpty(id) && StringUtils.isEmpty(title))
            throw new BadRequestException("Must include movie title or ID.");

        if (id != null) {
            var local = movieRepository.findById(id);

            var fetched = local.map(movie -> new MovieResponseCommand(movie, plot))
                    .orElseGet(() -> fetchFromOMDB(id, title, type, year, plot));

            if (fetched == null) throw new NoContentException();
            else return fetched;
        } else {
            var fetched = fetchFromOMDB(id, title, type, year, plot);
            if (fetched == null) throw new NoContentException();
            else return fetched;
        }

    }

    private MovieResponseCommand fetchMovie(String id) {
        return fetchMovie(id, null, null, null, "short");
    }

    public MoviePageResponseCommand fetchMoviePage(String title, Type type, String year, Integer page) {
        if (StringUtils.isEmpty(title)) throw new BadRequestException("Must include movie title.");

        var uri = UriComponentsBuilder.newInstance()
                .scheme("http").host("www.omdbapi.com")
                .path("/")
                .queryParam("apikey", API_KEY);
        uri.queryParam("s", URLDecoder.decode(title));
        if (type != null) uri.queryParam("type", type.toString());
        if (year != null) uri.queryParam("y", year);
        if (page != null) uri.queryParam("page", page);

        uri.buildAndExpand();

        OmdbSearchPageCommand response;
        try {
            response = new RestTemplate().getForEntity(new URI(uri.toUriString()), OmdbSearchPageCommand.class).getBody();
        } catch (URISyntaxException e) {
            throw new InternalServerErrorException();
        }
        if (response == null) throw new BadRequestException();
        var movies = response.getSearch().stream().map(movie -> fetchMovie(movie.getImdbID()));

        var moviePage = new MoviePageResponseCommand();
        moviePage.setTotalMovies(response.getTotalResults());
        movies.forEach(movie -> moviePage.getMovies().add(movie));

        return moviePage;
    }

    private MovieResponseCommand fetchFromOMDB(String id, String title, Type type, String year, String plot) {
        var uri = UriComponentsBuilder.newInstance()
                .scheme("http").host("www.omdbapi.com")
                .path("/")
                .queryParam("apikey", API_KEY);

        if (id != null) uri.queryParam("i", id);
        else if (title != null) uri.queryParam("t", title);
        else throw new BadRequestException();

        if (type != null) uri.queryParam("type", type.toString());
        if (year != null) uri.queryParam("y", year);
        if (plot != null) uri.queryParam("plot", plot);

        uri.buildAndExpand();

        var movie = new RestTemplate().getForEntity(uri.toUriString(), OmdbMovieResponseCommand.class).getBody();
        if (movie == null) return null;
        movieAsyncService.saveMovieIfNotExist(id);

        return new MovieResponseCommand(movie);
    }
}
