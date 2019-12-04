package com.example.movie_nights_rest.command.movie;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OmdbSearchPageCommand {

    @JsonProperty("Search")
    @Builder.Default
    private ArrayList<OmdbMovieShortResponseCommand> search = new ArrayList<>();

    @JsonProperty("totalResults")
    private Integer totalResults;
}
