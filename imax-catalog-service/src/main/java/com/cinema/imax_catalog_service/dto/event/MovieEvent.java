package com.cinema.imax_catalog_service.dto.event;

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

    private String eventId;  // UUID único para idempotencia
    private String eventType;  // SYNC_COMPLETED, MOVIE_ADDED, MOVIE_UPDATED, MOVIE_REMOVED
    private LocalDateTime timestamp;
    private SyncEventData data;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SyncEventData {
        // Metadata de la sincronización
        private Integer totalMoviesInCatalog;
        private Integer moviesAdded;
        private Integer moviesUpdated;
        private Integer moviesRemoved;
        private String syncSource;  // "TMDB"

        // Lista de películas sincronizadas (opcional, para enviar las películas completas)
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

        // URL completa del poster (helper)
        public String getFullPosterUrl() {
            if (posterPath != null && !posterPath.isEmpty()) {
                return "https://image.tmdb.org/t/p/w500" + posterPath;
            }
            return null;
        }
    }
}