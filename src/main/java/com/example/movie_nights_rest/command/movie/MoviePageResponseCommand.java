package com.example.movie_nights_rest.command.movie;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MoviePageResponseCommand {

    @Builder.Default
    private ArrayList<MovieResponseCommand> movies = new ArrayList<>();

    private Integer totalMovies;
}
