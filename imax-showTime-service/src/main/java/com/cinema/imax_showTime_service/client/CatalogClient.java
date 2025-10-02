package com.cinema.imax_showTime_service.client;

import com.cinema.imax_showTime_service.dto.MovieDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(
        name = "imax-catalog-service", // Nombre registrado en Eureka
        path = "/catalog-service/api/movies", // Prefijo de las rutas
        fallback = CatalogClientFallback.class
)
public interface CatalogClient {

    @GetMapping("/now-playing")
    List<MovieDTO> getNowPlayingMovies();

    @GetMapping("/{id}")
    MovieDTO getMovieById(@PathVariable("id") Integer id);

    @GetMapping("/{id}/exists")
    Boolean movieExists(@PathVariable("id") Integer id);

}
