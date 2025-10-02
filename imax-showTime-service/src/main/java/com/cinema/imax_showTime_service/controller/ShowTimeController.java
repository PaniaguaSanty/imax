package com.cinema.imax_showTime_service.controller;

import com.cinema.imax_showTime_service.dto.MovieDTO;
import com.cinema.imax_showTime_service.dto.ShowTimeWithMovieDTO;
import com.cinema.imax_showTime_service.model.ShowTime;
import com.cinema.imax_showTime_service.service.ShowTimeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/showTime-service/api/showtimes")
@RequiredArgsConstructor
@Slf4j
public class ShowTimeController {

    private final ShowTimeService showTimeService;

    // ==================== CRUD BÁSICO ====================

    /**
     * Crear nueva función
     */
    @PostMapping
    public ResponseEntity<ShowTime> createShowTime(@RequestBody ShowTime showTime) {
        try {
            ShowTime created = showTimeService.createShowTime(showTime);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            log.error("Invalid movie ID: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Obtener todas las funciones
     */
    @GetMapping
    public ResponseEntity<List<ShowTime>> getAllShowTimes() {
        return ResponseEntity.ok(showTimeService.getAllShowTimes());
    }

    /**
     * Obtener función por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ShowTime> getShowTimeById(@PathVariable Long id) {
        return showTimeService.getShowTimeById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Obtener funciones por fecha (solo datos de ShowTime)
     */
    @GetMapping("/date/{date}")
    public ResponseEntity<List<ShowTime>> getShowTimesByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(showTimeService.getShowTimesByDate(date));
    }

    // ==================== ENDPOINTS CON INFO DE PELÍCULAS ====================

    /**
     * ✅ Obtener funciones con información de película por fecha
     */
    @GetMapping("/date/{date}/with-movies")
    public ResponseEntity<List<ShowTimeWithMovieDTO>> getShowTimesWithMovieInfo(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(showTimeService.getShowTimesWithMovieInfo(date));
    }

    /**
     * ✅ Obtener funciones de una película específica (con info de película)
     */
    @GetMapping("/movie/{movieId}")
    public ResponseEntity<List<ShowTimeWithMovieDTO>> getShowTimesByMovie(
            @PathVariable Integer movieId) {
        return ResponseEntity.ok(showTimeService.getShowTimesByMovieWithInfo(movieId));
    }

    // ==================== ENDPOINTS DEL CATÁLOGO (Proxy) ====================

    /**
     * ✅ Obtener películas en cartelera desde el catálogo
     */
    @GetMapping("/catalog/now-playing")
    public ResponseEntity<List<MovieDTO>> getNowPlayingMovies() {
        List<MovieDTO> movies = showTimeService.getNowPlayingMovies();
        return ResponseEntity.ok(movies);
    }

    /**
     * ✅ Obtener película por ID desde el catálogo
     */
    @GetMapping("/catalog/movies/{movieId}")
    public ResponseEntity<MovieDTO> getMovieById(@PathVariable Integer movieId) {
        MovieDTO movie = showTimeService.getMovieById(movieId);
        if (movie == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(movie);
    }

    /**
     * ✅ Verificar si una película existe en el catálogo
     */
    @GetMapping("/catalog/movies/{movieId}/exists")
    public ResponseEntity<Boolean> movieExists(@PathVariable Integer movieId) {
        Boolean exists = showTimeService.movieExists(movieId);
        return ResponseEntity.ok(exists);
    }

    // ==================== RESERVAS ====================

    /**
     * ✅ Reservar asientos
     */
    @PatchMapping("/{id}/reserve")
    public ResponseEntity<ShowTime> reserveSeats(
            @PathVariable Long id,
            @RequestParam Integer seats) {
        try {
            ShowTime updated = showTimeService.reserveSeats(id, seats);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            log.error("ShowTime not found: {}", id);
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            log.error("Cannot reserve seats: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }
}