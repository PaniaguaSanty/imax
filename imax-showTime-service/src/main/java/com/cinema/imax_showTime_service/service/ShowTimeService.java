package com.cinema.imax_showTime_service.service;

import com.cinema.imax_showTime_service.client.CatalogClient;
import com.cinema.imax_showTime_service.dto.MovieDTO;
import com.cinema.imax_showTime_service.dto.ShowTimeWithMovieDTO;
import com.cinema.imax_showTime_service.model.ShowTime;
import com.cinema.imax_showTime_service.repository.ShowTimeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShowTimeService {

    private final ShowTimeRepository showTimeRepository;
    private final CatalogClient catalogClient; // Client feign inyected.

    /**
     * Crear una nueva función (validando que la película existe)
     */
    @Transactional
    public ShowTime createShowTime(ShowTime showTime) {
        log.info("Creating showtime for movieId: {}", showTime.getMovieId());

        // ✅ Validar que la película existe en el catálogo
        Boolean movieExists = catalogClient.movieExists(showTime.getMovieId());
        if (!Boolean.TRUE.equals(movieExists)) {
            log.error("Movie with ID {} does not exist in catalog", showTime.getMovieId());
            throw new IllegalArgumentException("Movie with ID " + showTime.getMovieId() + " not found in catalog");
        }

        // Validar asientos disponibles
        if (showTime.getAvailableSeats() == null) {
            showTime.setAvailableSeats(showTime.getMaxCapacity());
        }

        ShowTime saved = showTimeRepository.save(showTime);
        log.info("ShowTime created successfully with ID: {}", saved.getId());
        return saved;
    }

    /**
     * Obtener todas las funciones
     */
    public List<ShowTime> getAllShowTimes() {
        return showTimeRepository.findAll();
    }

    /**
     * Obtener función por ID
     */
    public Optional<ShowTime> getShowTimeById(Long id) {
        return showTimeRepository.findById(id);
    }

    /**
     * Obtener funciones por fecha
     */
    public List<ShowTime> getShowTimesByDate(LocalDate date) {
        return showTimeRepository.findByShowDate(date);
    }

    /**
     * Obtener funciones por película (solo ShowTime)
     */
    public List<ShowTime> getShowTimesByMovie(Integer movieId) {
        return showTimeRepository.findByMovieId(movieId);
    }

    /**
     * ✅ Obtener funciones de una fecha CON información de película
     */
    public List<ShowTimeWithMovieDTO> getShowTimesWithMovieInfo(LocalDate date) {
        log.info("Fetching showtimes with movie info for date: {}", date);

        List<ShowTime> showTimes = showTimeRepository.findByShowDate(date);

        return showTimes.stream()
                .map(this::enrichShowTimeWithMovieData)
                .collect(Collectors.toList());
    }

    /**
     * ✅ Obtener funciones de una película específica CON información de película
     */
    public List<ShowTimeWithMovieDTO> getShowTimesByMovieWithInfo(Integer movieId) {
        log.info("Fetching showtimes with movie info for movieId: {}", movieId);

        List<ShowTime> showTimes = showTimeRepository.findByMovieId(movieId);

        // Obtener la película una sola vez (optimización)
        MovieDTO movie = catalogClient.getMovieById(movieId);

        return showTimes.stream()
                .map(showTime -> new ShowTimeWithMovieDTO(showTime, movie))
                .collect(Collectors.toList());
    }

    /**
     * ✅ Helper: Enriquecer ShowTime con datos de película
     */
    private ShowTimeWithMovieDTO enrichShowTimeWithMovieData(ShowTime showTime) {
        try {
            MovieDTO movie = catalogClient.getMovieById(showTime.getMovieId());
            return new ShowTimeWithMovieDTO(showTime, movie);
        } catch (Exception e) {
            log.error("Error fetching movie data for movieId: {}", showTime.getMovieId(), e);
            // Si falla, devolver sin datos de película
            return new ShowTimeWithMovieDTO(showTime, null);
        }
    }

    /**
     * ✅ Obtener películas en cartelera (del catálogo)
     */
    public List<MovieDTO> getNowPlayingMovies() {
        log.info("Fetching now playing movies from catalog");
        return catalogClient.getNowPlayingMovies();
    }

    /**
     * ✅ Obtener película por ID (del catálogo)
     */
    public MovieDTO getMovieById(Integer id) {
        log.info("Fetching movie by ID: {}", id);
        return catalogClient.getMovieById(id);
    }

    /**
     * ✅ Verificar si una película existe (del catálogo)
     */
    public Boolean movieExists(Integer id) {
        log.info("Checking if movie exists: {}", id);
        return catalogClient.movieExists(id);
    }

    /**
     * ✅ BONUS: Reservar asientos (implementación básica)
     */
    @Transactional
    public ShowTime reserveSeats(Long showTimeId, Integer seats) {
        log.info("Reserving {} seats for showtime {}", seats, showTimeId);

        ShowTime showTime = showTimeRepository.findById(showTimeId)
                .orElseThrow(() -> new IllegalArgumentException("ShowTime not found: " + showTimeId));

        if (showTime.getAvailableSeats() < seats) {
            throw new IllegalStateException("Not enough available seats. Available: " + showTime.getAvailableSeats());
        }

        showTime.setAvailableSeats(showTime.getAvailableSeats() - seats);
        ShowTime updated = showTimeRepository.save(showTime);

        log.info("Reservation successful. Remaining seats: {}", updated.getAvailableSeats());
        return updated;
    }
}