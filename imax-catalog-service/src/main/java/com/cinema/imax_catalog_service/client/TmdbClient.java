package com.cinema.imax_catalog_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name = "tmdb-client", url = "${tmdb.base-url}")
public interface TmdbClient {

    @GetMapping("/search/movie")
    Map<String, Object> searchMovie(
            @RequestParam("api_key") String apiKey,
            @RequestParam("query") String query
    );
/*
    @GetMapping("/movie/{id}")
    MovieDetailsResponse getMovieDetails(@PathVariable("id") Integer id,
                                         @RequestParam("api_key") String apiKey);

    @GetMapping("/movie/{id}/credits")
    MovieCreditsResponse getMovieCredits(@PathVariable("id") Integer id,
                                         @RequestParam("api_key") String apiKey);

    @GetMapping("/movie/popular")
    MovieListResponse getPopularMovies(@RequestParam("api_key") String apiKey);

    @GetMapping("/movie/{id}/images")
    MovieImagesResponse getMovieImages(@PathVariable("id") Integer id,
                                       @RequestParam("api_key") String apiKey);


 */

}



