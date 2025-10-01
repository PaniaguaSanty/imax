package com.cinema.imax_catalog_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name = "tmdb-client", url = "${tmdb.base-url}")
public interface TmdbClient {

    @GetMapping("/movie/now_playing")
    Map<String, Object> getNowPlaying(
            @RequestParam("api_key") String apiKey
    );

}



