package com.cinema.imax_showTime_service.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MovieDTO {

    private Integer id;
    private String title;
    private String originalTitle;
    private String overview;
    private String posterPath;
    private String releaseDate;
    private List<Integer> genreIds;
    private String fullPosterUrl;

    // Helper to build full URL
    public String getFullPosterUrl() {
        if (posterPath != null && !posterPath.isEmpty()) {
            return "https://image.tmdb.org/t/p/w500" + posterPath;
        }
        return fullPosterUrl;
    }
}
