package com.cinema.imax_showTime_service.client;

import com.cinema.imax_showTime_service.dto.MovieDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
@Slf4j
public class CatalogClientFallback implements CatalogClient {

    @Override
    public List<MovieDTO> getNowPlayingMovies() {
        log.error("FALLBACK: CatalogService unavailable - returning empty movie list.");
        return Collections.emptyList();
    }

    @Override
    public MovieDTO getMovieById(Integer id) {
        log.error("FALLBACK: CatalogService unavailable - cannot get movie with ID: {}", id);
        return null;
    }

    @Override
    public Boolean movieExists(Integer id) {
        log.error("FALLBACK: CatalogService unavailable - cannot verify movie ID: {}", id);
        return false;
    }
}
