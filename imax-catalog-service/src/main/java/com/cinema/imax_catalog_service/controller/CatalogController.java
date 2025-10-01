package com.cinema.imax_catalog_service.controller;

import com.cinema.imax_catalog_service.dto.response.MovieResponse;
import com.cinema.imax_catalog_service.service.CatalogService;
import com.cinema.imax_catalog_service.service.MovieSyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CatalogController {

    private final CatalogService catalogService;
    private final MovieSyncService movieSyncService;

    /**
     * Endpoint to obtain the actual catalog. (Movies from the db).
     */
    @GetMapping("/movies/now-playing")
    public List<MovieResponse> getNowPlaying() {
        return catalogService.getNowPlayingMovies();
    }

    @GetMapping("/movies/sync-test")
    public String syncTest() {
        movieSyncService.syncNowPlayingMovies();
        return "Cartelera sincronizada!";
    }




}
