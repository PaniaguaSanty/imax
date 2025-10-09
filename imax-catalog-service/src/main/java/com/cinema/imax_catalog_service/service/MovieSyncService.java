package com.cinema.imax_catalog_service.service;

import com.cinema.imax_catalog_service.dto.event.MovieEvent;
import com.cinema.imax_catalog_service.model.Movie;
import com.cinema.imax_catalog_service.repository.MovieRepository;
import com.cinema.imax_catalog_service.client.TmdbClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MovieSyncService {

    private final TmdbClient tmdbClient;
    private final MovieRepository movieRepository;
    private final MovieEventProducer movieEventProducer;

    @Value("${tmdb.api-key}")
    private String apiKey;

    public MovieSyncService(TmdbClient tmdbClient,
                            MovieRepository movieRepository,
                            MovieEventProducer movieEventProducer) {
        this.tmdbClient = tmdbClient;
        this.movieRepository = movieRepository;
        this.movieEventProducer = movieEventProducer;
    }

    /**
     * Sync every day at 3am
     */
    @Scheduled(cron = "0 0 3 * * ?")
    @Transactional
    public void syncNowPlayingMovies() {
        log.info("🎬 Sync started...");

        try {
            List<Movie> oldMovies = movieRepository.findAll();
            Set<Integer> oldMovieIds = oldMovies.stream()
                    .map(Movie::getId)
                    .collect(Collectors.toSet());

            // 2. Obtain movies from TMDB
            Map<String, Object> response = tmdbClient.getNowPlaying(apiKey);
            List<Map<String, Object>> results = (List<Map<String, Object>>) response.get("results");

            if (results == null || results.isEmpty()) {
                log.warn("⚠️ No se obtuvieron películas de TMDB");
                return;
            }

            // 3. Process every movie to obtain complex details
            List<Movie> newMovies = new ArrayList<>();
            int successCount = 0;
            int failCount = 0;

            for (Map<String, Object> movieMap : results) {
                try {
                    Movie movie = mapToEntityWithDetails(movieMap);
                    newMovies.add(movie);
                    successCount++;
                } catch (Exception e) {
                    failCount++;
                    log.error("❌ Error loading movie with ID: {}, error: {}",
                            movieMap.get("id"), e.getMessage());
                }
            }

            // 4. Calcular estadísticas de la sincronización
            Set<Integer> newMovieIds = newMovies.stream()
                    .map(Movie::getId)
                    .collect(Collectors.toSet());

            int moviesAdded = (int) newMovieIds.stream()
                    .filter(id -> !oldMovieIds.contains(id))
                    .count();

            int moviesRemoved = (int) oldMovieIds.stream()
                    .filter(id -> !newMovieIds.contains(id))
                    .count();

            int moviesUpdated = (int) newMovieIds.stream()
                    .filter(oldMovieIds::contains)
                    .count();

            // 5. Refresh database
            if (!newMovies.isEmpty()) {
                movieRepository.deleteAll();
                movieRepository.saveAll(newMovies);

                log.info("✅ Completed sync: {} saved movies, {} errors",
                        successCount, failCount);
                log.info("📊 Stats - Added: {}, Updated: {}, Removed: {}",
                        moviesAdded, moviesUpdated, moviesRemoved);

                // 6. Publish event on Kafka
                publishSyncEvent(newMovies, moviesAdded, moviesUpdated, moviesRemoved);

            } else {
                log.warn("⚠️ Couldn't save movies in the db.");
            }

        } catch (Exception e) {
            log.error("❌ Critical error during sync: {}", e.getMessage(), e);
        }
    }

    /**
     * Publica el evento de sincronización en Kafka
     */
    private void publishSyncEvent(List<Movie> movies, int added, int updated, int removed) {
        try {
            // Convertir entidades Movie a MovieData DTO
            List<MovieEvent.MovieData> movieDataList = movies.stream()
                    .map(this::convertToMovieData)
                    .collect(Collectors.toList());

            // Crear el evento de sincronización
            MovieEvent.SyncEventData syncData = MovieEvent.SyncEventData.builder()
                    .totalMoviesInCatalog(movies.size())
                    .moviesAdded(added)
                    .moviesUpdated(updated)
                    .moviesRemoved(removed)
                    .syncSource("TMDB")
                    .movies(movieDataList)
                    .build();

            // Publicar en Kafka
            movieEventProducer.publishSyncCompletedEvent(syncData);

            log.info("📨 Evento de sincronización enviado a Kafka");

        } catch (Exception e) {
            // No fallar la sincronización si Kafka falla
            log.error("⚠️ Error al publicar evento en Kafka (pero sync completado): {}",
                    e.getMessage(), e);
        }
    }

    /**
     * Convierte Movie entity a MovieData DTO
     */
    private MovieEvent.MovieData convertToMovieData(Movie movie) {
        return MovieEvent.MovieData.builder()
                .id(movie.getId())
                .title(movie.getTitle())
                .originalTitle(movie.getOriginalTitle())
                .overview(movie.getOverview())
                .releaseDate(movie.getReleaseDate())
                .duration(movie.getDuration())
                .rating(movie.getRating())
                .posterPath(movie.getPosterPath())
                .genreIds(movie.getGenreIds())
                .build();
    }

    /**
     * Extract rating from US (PG, PG-13, R, etc.)
     */
    private String extractUsRating(Map<String, Object> details) {
        try {
            Map<String, Object> releaseDates = (Map<String, Object>) details.get("release_dates");
            if (releaseDates == null) return null;

            List<Map<String, Object>> results =
                    (List<Map<String, Object>>) releaseDates.get("results");

            if (results == null) return null;

            for (Map<String, Object> country : results) {
                if ("US".equals(country.get("iso_3166_1"))) {
                    List<Map<String, Object>> releaseDatesList =
                            (List<Map<String, Object>>) country.get("release_dates");

                    if (releaseDatesList != null && !releaseDatesList.isEmpty()) {
                        String certification = (String) releaseDatesList.get(0).get("certification");
                        if (certification != null && !certification.isEmpty()) {
                            return certification;
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.warn("⚠️ Couldn't extract rating: {}", e.getMessage());
        }
        return null;
    }

    /**
     * Extract list of genre IDs
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
     * Extract duration in minutes
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

        Map<String, Object> details = tmdbClient.getMovieDetails(movieId, apiKey, "release_dates");

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