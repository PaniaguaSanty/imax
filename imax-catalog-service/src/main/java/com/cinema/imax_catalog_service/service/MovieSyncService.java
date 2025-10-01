package com.cinema.imax_catalog_service.service;

import com.cinema.imax_catalog_service.model.Movie;
import com.cinema.imax_catalog_service.repository.MovieRepository;
import com.cinema.imax_catalog_service.client.TmdbClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

//this class refresh the catalog automatically.
@Service
public class MovieSyncService {

    private final TmdbClient tmdbClient;
    private final MovieRepository movieRepository;
    private static final Logger logger = Logger.getLogger(MovieSyncService.class.getName());

    @Value("${tmdb.api-key}")
    private String apiKey;

    public MovieSyncService(TmdbClient tmdbClient, MovieRepository movieRepository) {
        this.tmdbClient = tmdbClient;
        this.movieRepository = movieRepository;
    }

    // Scheduler: se ejecuta todos los días a las 3am
    @Scheduled(cron = "0 0 3 * * ?")
    public void syncNowPlayingMovies() {
        logger.info("Sincronizando películas...");

        Map<String, Object> response = tmdbClient.getNowPlaying(apiKey); // <-- aquí
        List<Map<String, Object>> results = (List<Map<String, Object>>) response.get("results");

        if (results == null || results.isEmpty()) {
            logger.warning("No se obtuvieron películas de TMDB.");
            return;
        }

        List<Movie> movies = results.stream()
                .map(this::mapToEntity)
                .collect(Collectors.toList());

        movieRepository.deleteAll();
        movieRepository.saveAll(movies);

        logger.info("Se sincronizaron " + movies.size() + " películas.");
    }


    private Movie mapToEntity(Map<String, Object> movieMap) {
        return Movie.builder()
                .id(movieMap.get("id") != null ? ((Number) movieMap.get("id")).intValue() : null)
                .title((String) movieMap.get("title"))
                .overview((String) movieMap.get("overview"))
                .releaseDate((String) movieMap.get("release_date"))
                .posterPath((String) movieMap.get("poster_path"))
                .genreIds(movieMap.get("genre_ids") != null
                        ? ((List<?>) movieMap.get("genre_ids")).stream()
                        .map(n -> ((Number) n).intValue())
                        .collect(Collectors.toList())
                        : null)
                .build();
    }


}
