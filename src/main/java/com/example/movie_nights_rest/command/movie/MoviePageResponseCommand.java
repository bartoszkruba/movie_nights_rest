package com.example.movie_nights_rest.command.movie;

import com.fasterxml.jackson.annotation.JsonInclude;
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
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel("Movie page")
public class MoviePageResponseCommand {

    @Builder.Default
    @ApiModelProperty("Movies")
    private ArrayList<MovieResponseCommand> movies = new ArrayList<>();

    @ApiModelProperty("Total matching movies in the database")
    private Integer totalMovies;
}
