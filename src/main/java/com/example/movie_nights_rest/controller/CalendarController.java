package com.example.movie_nights_rest.controller;

import com.example.movie_nights_rest.annotation.CurrentUser;
import com.example.movie_nights_rest.command.movieWatching.MovieWatchingCommand;
import com.example.movie_nights_rest.config.security.UserPrincipal;
import com.example.movie_nights_rest.model.Role;
import com.example.movie_nights_rest.service.CalendarService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequestMapping("/api/calendar")
@RequiredArgsConstructor
@CrossOrigin
public class CalendarController {

    private final CalendarService calendarService;

    @PostMapping("/me/movieWatching")
    @Secured({Role.ADMIN, Role.BASIC})
    @ApiOperation("Create new movie watching. Available for registered users.")
    @ResponseStatus(HttpStatus.CREATED)
    public MovieWatchingCommand createMovieWatching(
            @ApiIgnore @CurrentUser UserPrincipal userPrincipal,
            @ApiParam("Users IDs. Have to be your friends.") @RequestParam String[] attendees,
            @ApiParam("Epoch timestamp for event start") @RequestParam Long startTime,
            @ApiParam("ID of the movie") @RequestParam String movieId) {
        return calendarService.createMovieWatching(userPrincipal.getId(), attendees, startTime, movieId);
    }

    @GetMapping("/me/movieWatching")
    @Secured({Role.ADMIN, Role.BASIC})
    @ApiOperation("Get your upcoming movie watching events. Available for registered users.")
    public Iterable<MovieWatchingCommand> getMovieWatching(@ApiIgnore @CurrentUser UserPrincipal userPrincipal) {
        return calendarService.getMovieWatchings(userPrincipal.getRefreshToken());
    }

    @GetMapping("/me/movieWatching/possibleTimes")
    @Secured({Role.ADMIN, Role.BASIC})
    @ApiOperation("Fetch possible times (epoch timestamp) for movie watching event of certain movie with certain users." +
            "Available for registered users")
    public Iterable<Long> getPossibleWatchingTimes(
            @ApiParam(value = "Wishing start time expressed in minutes after midnight", defaultValue = "0")
            @RequestParam
                    Integer startTime,
            @ApiParam("Movie ID") @RequestParam String movieId,
            @ApiParam(value = "How many possible times you want to fetch", defaultValue = "7")
            @RequestParam(defaultValue = "7")
                    Integer count,
            @ApiParam("IDs of users you wish to watch movie with")
            @RequestParam
                    String[] attendees) {
        return calendarService.getPossibleWatchingTimes(attendees, startTime, movieId, count);
    }

}
