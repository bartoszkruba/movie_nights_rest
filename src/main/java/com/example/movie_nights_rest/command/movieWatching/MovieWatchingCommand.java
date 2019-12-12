package com.example.movie_nights_rest.command.movieWatching;


import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel("Movie watching event")
public class MovieWatchingCommand {

    @ApiModelProperty("Movie ID")
    private String movieId;

    @ApiModelProperty("Event start time in epoch milliseconds")
    private Long startTime;

    @ApiModelProperty("Event end time in epoch milliseconds")
    private Long endTime;

    @ApiModelProperty("IDs for all the attendees")
    private List<String> attendees;
}
