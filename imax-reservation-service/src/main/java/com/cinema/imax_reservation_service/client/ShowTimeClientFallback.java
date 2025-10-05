package com.cinema.imax_reservation_service.client;

import com.cinema.imax_reservation_service.dto.ShowTimeDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ShowTimeClientFallback implements ShowTimeClient {

    @Override
    public ShowTimeDTO getShowTimeById(Long id) {
        log.error("Fallback: Unable to get ShowTime with id: {}", id);
        throw new RuntimeException("ShowTime service is currently unavailable");
    }

    @Override
    public ShowTimeDTO reserveSeats(Long id, Integer seats) {
        log.error("Fallback: Unable to reserve {} seats for ShowTime: {}", seats, id);
        throw new RuntimeException("ShowTime service is currently unavailable");
    }
}
