package com.example.movie_nights_rest.repository;

import com.example.movie_nights_rest.model.Movie;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface MovieRepository extends ReactiveMongoRepository<Movie, String> {
}
