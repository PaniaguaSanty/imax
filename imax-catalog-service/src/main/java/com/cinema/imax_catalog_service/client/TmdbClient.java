package com.cinema.imax_catalog_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name = "tmdb-client", url = "${tmdb.base-url}")
public interface TmdbClient {

    /**
     * Obtain movies on catalog.
     */
    @GetMapping("/movie/now_playing")
    Map<String, Object> getNowPlaying(@RequestParam("api_key") String apiKey);

    /**
     * Obtain complex details from a movie,
     * Includes: runtime (duration), certification (rating), etc.
     */
    @GetMapping("/movie/{movieId}")
    Map<String, Object> getMovieDetails(
            @PathVariable("movieId") Integer movieId,
            @RequestParam("api_key") String apiKey,
            @RequestParam(value = "append_to_response", defaultValue = "release_dates") String appendToResponse
    );
}