package com.example.movie_nights_rest.command.movieWatching;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("Movie watching request")
public class MovieWatchingRequestCommand {

    @ApiModelProperty("Users IDs. Have to be your friends.")
    private ArrayList<String> attendees = new ArrayList<>();

    @ApiModelProperty("ID of the movie you'd like to watch.")
    private String movieId;

    @ApiModelProperty("Epoch timestamp for event start.")
    private Long startTime;

    @ApiModelProperty("Location of the event")
    private String location;
}
