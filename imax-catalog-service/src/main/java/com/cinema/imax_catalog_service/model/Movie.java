package com.cinema.imax_catalog_service.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "movies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Movie {


    @Id
    private Integer id;

    @Column(length = 1000)
    private String overview;

    @Column(length = 500)
    private String title;

    @Column(name = "original_title", length = 500)
    private String originalTitle;

    @Column(name = "poster_path", length = 500)
    private String posterPath;

    @Column(name = "release_date", length = 255)
    private String releaseDate;


    @ElementCollection
    private List<Integer> genreIds;
}
