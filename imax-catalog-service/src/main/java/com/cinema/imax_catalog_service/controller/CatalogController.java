package com.cinema.imax_catalog_service.controller;

import com.cinema.imax_catalog_service.service.TmdbService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class CatalogController {

    private final TmdbService tmdbService;

    public CatalogController(TmdbService tmdbService) {
        this.tmdbService = tmdbService;
    }

    @GetMapping("/tmdb/search")
    public Map<String, Object> searchMovie(@RequestParam String query) {
        return tmdbService.searchMovie(query);
    }
}
