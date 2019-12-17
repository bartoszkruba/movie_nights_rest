package com.example.movie_nights_rest.config.logging;

import com.example.movie_nights_rest.model.RequestResponseLog;
import com.example.movie_nights_rest.repository.RequestResponseLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.trace.http.HttpTrace;
import org.springframework.boot.actuate.trace.http.HttpTraceRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Repository
@RequiredArgsConstructor
public class CustomTraceRepository implements HttpTraceRepository {

    private final RequestResponseLogRepository requestResponseLogRepository;

    @Override
    public List<HttpTrace> findAll() {
        return StreamSupport.stream(requestResponseLogRepository.findAll().spliterator(), false)
                .map(RequestResponseLog::toHttpTrace)
                .collect(Collectors.toList());
    }

    @Override
    public void add(HttpTrace trace) {
        requestResponseLogRepository.save(new RequestResponseLog(trace));
    }
}
