package com.example.movie_nights_rest.service;

import com.example.movie_nights_rest.model.RequestResponseLog;
import com.example.movie_nights_rest.repository.RequestResponseLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoggingService {

    private final RequestResponseLogRepository requestResponseLogRepository;

    public Iterable<RequestResponseLog> getAll() {
        return requestResponseLogRepository.findAll();
    }

    public Iterable<RequestResponseLog> getPage(int page) {
        var pageRequest = PageRequest.of(page, 10, Sort.by("timestamp").descending());
        return requestResponseLogRepository.findAll(pageRequest);
    }
}
