/*package com.cinema.imax_catalog_service.controller;


import com.cinema.imax_catalog_service.service.TmdbService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@Controller
public class HomeController {

    private final TmdbService tmdbService;

    public HomeController(TmdbService tmdbService) {
        this.tmdbService = tmdbService;
    }

    @GetMapping("/")
    public String home(@RequestParam(value = "query", required = false) String query, Model model) {
        try {
            Map<String, Object> response;
            if (query != null && !query.trim().isEmpty()) {
                response = tmdbService.searchMovie(query);
            } else {
                // Buscar películas populares por defecto
                response = tmdbService.searchMovie("popular");
            }

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> movies = (List<Map<String, Object>>) response.get("results");

            if (movies != null && !movies.isEmpty()) {
                model.addAttribute("movies", movies.subList(0, Math.min(movies.size(), 12)));
            } else {
                model.addAttribute("movies", List.of());
            }

            model.addAttribute("query", query);

        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar las películas: " + e.getMessage());
            model.addAttribute("movies", List.of());
        }

        return "home";
    }
}

 */