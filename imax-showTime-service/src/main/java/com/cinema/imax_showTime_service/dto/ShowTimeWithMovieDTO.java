package com.cinema.imax_showTime_service.dto;// ShowTimeWithMovieDTO.java

import com.cinema.imax_showTime_service.model.ShowTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShowTimeWithMovieDTO {
    private ShowTime showTime;
    private MovieDTO movie;

    public String getMovieTitle() {
        return movie != null ? movie.getTitle() : "Unknown";
    }

    public boolean isAvailable() {
        return showTime != null && showTime.getAvailableSeats() > 0;
    }
}