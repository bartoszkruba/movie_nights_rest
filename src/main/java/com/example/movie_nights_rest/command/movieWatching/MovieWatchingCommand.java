package com.example.movie_nights_rest.command.movieWatching;

import com.example.movie_nights_rest.model.MovieWatching;
import com.example.movie_nights_rest.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieWatchingCommand {

    private Long startTime;

    private Long endTime;

    private List<String> attendees;

    public MovieWatchingCommand(MovieWatching movieWatching) {
        ZoneId zoneId = ZoneId.systemDefault();
        this.startTime = movieWatching.getStartTime().atZone(zoneId).toInstant().toEpochMilli();
        this.endTime = movieWatching.getEndTime().atZone(zoneId).toInstant().toEpochMilli();
        this.attendees = movieWatching.getAttendees().stream().map(User::getId).collect(Collectors.toList());
    }
}
