package com.cinema.imax_showTime_service.repository;

import com.cinema.imax_showTime_service.model.ShowTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface ShowTimeRepository extends JpaRepository<ShowTime, Long> {

    List<ShowTime> findByMovieId(Integer movieId);

    List<ShowTime> findByShowDate(LocalDate date);

    List<ShowTime> findByMovieIdAndShowDate(Integer movieId, LocalDate date);

    // Validate scheduling conflicts
    boolean existsByRoomAndShowDateAndShowTime(
            String room,
            LocalDate showDate,
            LocalTime showTime
    );

    // Get available shows (with seats)
    @Query("SELECT s FROM ShowTime s WHERE s.showDate = :date AND s.availableSeats > 0")
    List<ShowTime> findAvailableShowTimesByDate(LocalDate date);

    // Get future showtimes of a movie
    @Query("SELECT s FROM ShowTime s WHERE s.movieId = :movieId AND s.showDate >= :date ORDER BY s.showDate, s.showTime")
    List<ShowTime> findUpcomingShowTimesByMovie(Integer movieId, LocalDate date);
}
