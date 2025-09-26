package com.cinema.imax_catalog_service.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "movies")
public class Movie {
    private Integer id;
    private String title;
    private String originalTitle;
    private String overview;
    private String releaseDate;
    private Double voteAverage;
    private Integer voteCount;
    private List<Integer> genreIds;
    private String posterPath;
    private String backdropPath;
    private Boolean adult;
    private Boolean video;

}
