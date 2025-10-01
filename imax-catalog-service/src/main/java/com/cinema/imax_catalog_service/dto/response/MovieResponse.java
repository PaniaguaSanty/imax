package com.cinema.imax_catalog_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovieResponse {

    private Integer id;
    private String title;
    private String overview;
    private String releaseDate;
    private String posterPath;
    private List<Integer> genreIds;
}
