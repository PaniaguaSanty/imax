package com.cinema.imax_catalog_service.service;

import com.cinema.imax_catalog_service.model.Movie;
import com.cinema.imax_catalog_service.repository.MovieRepository;
import com.cinema.imax_catalog_service.client.TmdbClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MovieSyncService {

    private final TmdbClient tmdbClient;
    private final MovieRepository movieRepository;

    @Value("${tmdb.api-key}")
    private String apiKey;

    public MovieSyncService(TmdbClient tmdbClient, MovieRepository movieRepository) {
        this.tmdbClient = tmdbClient;
        this.movieRepository = movieRepository;
    }

    /**
     * Sync every day at 3am
     */
    @Scheduled(cron = "0 0 3 * * ?")
    public void syncNowPlayingMovies() {
        log.info(" Sync started...");

        try {
            // 1. Obtain movies from catalog.
            Map<String, Object> response = tmdbClient.getNowPlaying(apiKey);
            List<Map<String, Object>> results = (List<Map<String, Object>>) response.get("results");

            if (results == null || results.isEmpty()) {
                log.warn("⚠ No se obtuvieron películas de TMDB");
                return;
            }

            // 2. Process every movie to obtain complex details.
            List<Movie> movies = new ArrayList<>();
            int successCount = 0;
            int failCount = 0;

            for (Map<String, Object> movieMap : results) {
                try {
                    Movie movie = mapToEntityWithDetails(movieMap);
                    movies.add(movie);
                    successCount++;
                } catch (Exception e) {
                    failCount++;
                    log.error("Error loading movie with ID: {}, error: {}",
                            movieMap.get("id"), e.getMessage());
                }
            }

            // 3. Refresh database.
            if (!movies.isEmpty()) {
                movieRepository.deleteAll();
                movieRepository.saveAll(movies);
                log.info("Completed sync: {} saved movies, {} errors",
                        successCount, failCount);
            } else {
                log.warn("Couldn´t save movies in the db.");
            }

        } catch (Exception e) {
            log.error("Critical error during sync: {}", e.getMessage(), e);
        }
    }

    /**
     *  Extract rating from EE-UU (PG, PG-13, R, etc.)
     */
    private String extractUsRating(Map<String, Object> details) {
        try {
            Map<String, Object> releaseDates = (Map<String, Object>) details.get("release_dates");
            if (releaseDates == null) return null;

            List<Map<String, Object>> results =
                    (List<Map<String, Object>>) releaseDates.get("results");

            if (results == null) return null;

            // Search certification on EEUU (iso_3166_1 = "US")
            for (Map<String, Object> country : results) {
                if ("US".equals(country.get("iso_3166_1"))) {
                    List<Map<String, Object>> releaseDatesList =
                            (List<Map<String, Object>>) country.get("release_dates");

                    if (releaseDatesList != null && !releaseDatesList.isEmpty()) {
                        // Take the first certification available.
                        String certification = (String) releaseDatesList.get(0).get("certification");
                        if (certification != null && !certification.isEmpty()) {
                            return certification;
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.warn("Couldn´t extract rating: {}", e.getMessage());
        }
        return null;
    }

    /**
     * Extract list of genre ID´s.
     */
    private List<Integer> extractGenreIds(Map<String, Object> movieMap) {
        Object genreIds = movieMap.get("genre_ids");
        if (genreIds instanceof List<?>) {
            return ((List<?>) genreIds).stream()
                    .filter(obj -> obj instanceof Number)
                    .map(obj -> ((Number) obj).intValue())
                    .collect(Collectors.toList());
        }
        return null;
    }

    /**
     * Extract duration in minutes.
     */
    private Integer extractDuration(Map<String, Object> details) {
        Object runtime = details.get("runtime");
        if (runtime instanceof Number) {
            return ((Number) runtime).intValue();
        }
        return null;
    }

    /**
     * Map basic movie & obtains complex details (duration + rating)
     */
    private Movie mapToEntityWithDetails(Map<String, Object> movieMap) {
        Integer movieId = movieMap.get("id") != null
                ? ((Number) movieMap.get("id")).intValue()
                : null;

        if (movieId == null) {
            throw new RuntimeException("Movie ID is null");
        }

        // Obtain complex details.
        Map<String, Object> details = tmdbClient.getMovieDetails(movieId, apiKey, "release_dates");

        // Construct entity with all the data.
        return Movie.builder()
                .id(movieId)
                .title((String) movieMap.get("title"))
                .originalTitle((String) details.get("original_title"))
                .overview((String) movieMap.get("overview"))
                .releaseDate((String) movieMap.get("release_date"))
                .posterPath((String) movieMap.get("poster_path"))
                .duration(extractDuration(details))
                .rating(extractUsRating(details))
                .genreIds(extractGenreIds(movieMap))
                .build();
    }
}