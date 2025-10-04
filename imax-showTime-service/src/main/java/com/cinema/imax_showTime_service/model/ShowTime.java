package com.cinema.imax_showTime_service.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "showTimes")
public class ShowTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer movieId;  // Movie ID in TMDB

    @Column(nullable = false, length = 50)
    private String room; // ej: "IMAX Theater", "Theater 1"

    @Column(nullable = false)
    private LocalDate showDate; // Function date

    @Column(nullable = false)
    private LocalTime showTime; //Function start time (was "duration" but should be "showTime")

    @Column(nullable = false)
    private Integer durationMinutes; //Duration in minutes (e.g., 120 for 2 hours)

    @Column(nullable = false)
    private Integer maxCapacity; // Maximum room capacity

    @Column(nullable = false)
    private Integer availableSeats; // Available seats.

    @Column(length = 20)
    private String format; // "2D", "3D", "IMAX", etc.

    @Column(precision = 10, scale = 2)
    private BigDecimal price;  //Use BigDecimal for monetary values

    // ==================== UTILITY METHODS ====================

    /**
     * Check if the function is exhausted
     */
    public boolean isSoldOut() {
        return availableSeats == 0;
    }

    /**
     * Gets the number of occupied seats
     */
    public Integer getOccupiedSeats() {
        return maxCapacity - availableSeats;
    }

    /**
     * Calculate the occupancy rate
     */
    public Double getOccupancyPercentage() {
        if (maxCapacity == 0) return 0.0;
        return (double) getOccupiedSeats() / maxCapacity * 100;
    }

    /**
     * Verify if there is available seats.
     */
    public boolean hasAvailableSeats(Integer requiredSeats) {
        return availableSeats >= requiredSeats;
    }
}