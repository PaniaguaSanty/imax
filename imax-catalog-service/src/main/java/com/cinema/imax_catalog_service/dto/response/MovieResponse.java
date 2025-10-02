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
    private String originalTitle;
    private String overview;
    private String releaseDate;
    private Integer duration;
    private String rating;    // PG, PG-13, R, etc.
    private String posterPath;
    private List<Integer> genreIds;

    /**
     * Helper method to construct the URL.
     */
    public String getFullPosterUrl() {
        if (posterPath != null && !posterPath.isEmpty()) {
            return "https://image.tmdb.org/t/p/w500" + posterPath;
        }
        return null;
    }

    /**
     * Helper to obtain the duration.
     */
    public String getFormattedDuration() {
        if (duration == null) return "N/A";
        int hours = duration / 60;
        int minutes = duration % 60;
        return hours + "h " + minutes + "m";
    }

    public boolean isFamilyFriendly() {
        if (rating == null) return false;
        return rating.equals("G") || rating.equals("PG") || rating.equals("PG-13");
    }
}