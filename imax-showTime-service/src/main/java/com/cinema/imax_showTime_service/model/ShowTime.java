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
    private Integer movieId;  // ID de la película en TMDB

    @Column(nullable = false, length = 50)
    private String room; // ej: "Sala IMAX", "Sala 1"

    @Column(nullable = false)
    private LocalDate showDate; // Fecha de la función

    @Column(nullable = false)
    private LocalTime duration; // ✅ Hora de inicio de la función (era "duration" pero debería ser "showTime")

    @Column(nullable = false)
    private Integer durationMinutes; // ✅ Duración en minutos (ej: 120 para 2 horas)

    @Column(nullable = false)
    private Integer maxCapacity; // Capacidad máxima de la sala

    @Column(nullable = false)
    private Integer availableSeats; // Asientos disponibles

    @Column(length = 20)
    private String format; // "2D", "3D", "IMAX", etc.

    @Column(precision = 10, scale = 2)
    private BigDecimal price;  // ✅ Usar BigDecimal para valores monetarios

    // ==================== MÉTODOS DE UTILIDAD ====================

    /**
     * Verifica si la función está agotada
     */
    public boolean isSoldOut() {
        return availableSeats == 0;
    }

    /**
     * Obtiene la cantidad de asientos ocupados
     */
    public Integer getOccupiedSeats() {
        return maxCapacity - availableSeats;
    }

    /**
     * Calcula el porcentaje de ocupación
     */
    public Double getOccupancyPercentage() {
        if (maxCapacity == 0) return 0.0;
        return (double) getOccupiedSeats() / maxCapacity * 100;
    }

    /**
     * Verifica si hay asientos suficientes disponibles
     */
    public boolean hasAvailableSeats(Integer requiredSeats) {
        return availableSeats >= requiredSeats;
    }
}