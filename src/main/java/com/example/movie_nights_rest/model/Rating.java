package com.example.movie_nights_rest.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("Rating")
public class Rating {

    @JsonProperty("Source")
    @ApiModelProperty("Rating source")
    private String source;

    @ApiModelProperty("Rating value")
    @JsonProperty("Value")
    private String value;
}
