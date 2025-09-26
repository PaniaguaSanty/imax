package com.cinema.imax_catalog_service.service;

import com.cinema.imax_catalog_service.client.TmdbClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class TmdbService {

    private final TmdbClient tmdbClient;

    @Value("${tmdb.api-key}")
    private String apiKey;

    public TmdbService(TmdbClient tmdbClient) {
        this.tmdbClient = tmdbClient;
    }

    public Map<String, Object> searchMovie(String query) {
        return tmdbClient.searchMovie(apiKey, query);
    }
}
