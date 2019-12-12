package com.example.movie_nights_rest.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailAsyncService {

    @Async
    public void sendMovieWatchingInfo(String email, String movieId) {

    }
}
