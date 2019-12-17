package com.example.movie_nights_rest.config.logging;

import com.example.movie_nights_rest.model.Log;
import com.example.movie_nights_rest.repository.LogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.trace.http.HttpTrace;
import org.springframework.boot.actuate.trace.http.HttpTraceRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class CustomTraceRepository implements HttpTraceRepository {

    private final LogRepository logRepository;

    @Override
    public List<HttpTrace> findAll() {
        var logs = logRepository.findAll();
        System.out.println(logs);
        return logRepository.findAll()
                .stream()
                .map(Log::toHttpTrace)
                .peek(System.out::println)
                .collect(Collectors.toList());
    }

    @Override
    public void add(HttpTrace trace) {
        logRepository.save(new Log(trace));
    }
}
