package com.cinema.imax_reservation_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ShowTimeDTO {

    private Long id;
    private Integer movieId;
    private String room;
    private LocalDate showDate;
    private LocalTime showTime;
    private Integer durationMinutes;
    private Integer maxCapacity;
    private Integer availableSeats;
    private String format;
    private BigDecimal price;

}
