package com.cinema.imax_catalog_service.controller;

import com.cinema.imax_catalog_service.dto.response.MovieResponse;
import com.cinema.imax_catalog_service.service.CatalogService;
import com.cinema.imax_catalog_service.service.MovieSyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
public class CatalogController {

    private final CatalogService catalogService;
    private final MovieSyncService movieSyncService;

    /**
     * Endpoint to sync the movies (useful for testing).
     */
    @GetMapping("/sync-test")
    public String syncTest() {
        movieSyncService.syncNowPlayingMovies();
        return "Cartelera sincronizada!";
    }

    /**
     * Endpoint to obtain the actual catalog. (Movies from the db).
     */
    @GetMapping("/now-playing")
    public List<MovieResponse> getNowPlaying() {
        return catalogService.getNowPlayingMovies();
    }

    /**
     * Endpoint to obtain a movie for id.
     */
    @GetMapping("/{id}")
    public ResponseEntity<MovieResponse> getMovieById(@PathVariable Integer id) {
        return catalogService.getMovieById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Endpoint to show if a movie exist.
     */
    @GetMapping("/{id}/exists")
    public ResponseEntity<Boolean> movieExists(@PathVariable Integer id) {
        return ResponseEntity.ok(catalogService.isMovieInCatalog(id));
    }

}
