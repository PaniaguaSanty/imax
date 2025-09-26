package com.cinema.imax_catalog_service.dto.response;

import com.cinema.imax_catalog_service.model.Movie;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MovieSearchResponse {
    private Integer page;
    private List<Movie> results;
}
