package com.cinema.imax_notification_service.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieEvent {

    private String eventId;
    private String eventType;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;

    private SyncEventData data;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SyncEventData {
        private Integer totalMoviesInCatalog;
        private Integer moviesAdded;
        private Integer moviesUpdated;
        private Integer moviesRemoved;
        private String syncSource;
        private List<MovieData> movies;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MovieData {
        private Integer id;
        private String title;
        private String originalTitle;
        private String overview;
        private String releaseDate;
        private Integer duration;
        private String rating;
        private String posterPath;
        private List<Integer> genreIds;
        private String fullPosterUrl;
    }
}