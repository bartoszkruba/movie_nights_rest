package com.example.movie_nights_rest.controller;

import com.example.movie_nights_rest.model.RequestResponseLog;
import com.example.movie_nights_rest.service.LoggingService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/logs")
@RequiredArgsConstructor
public class LoggingController {

    private final LoggingService loggingService;

    @GetMapping
    @ApiOperation("Get logs. 100 requests per page.")
    public Iterable<RequestResponseLog> getLogsPage(
            @ApiParam(value = "Page", defaultValue = "0") @RequestParam(defaultValue = "0") Integer page) {
        return loggingService.getPage(page);
    }

    @GetMapping("/all")
    @ApiOperation("Get all logs.")
    public Iterable<RequestResponseLog> getAllLogs() {
        return loggingService.getAll();
    }
}
