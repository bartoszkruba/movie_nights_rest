package com.example.movie_nights_rest.repository;

import com.example.movie_nights_rest.model.MovieWatching;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MovieWatchingRepository extends MongoRepository<MovieWatching, String> {
}
