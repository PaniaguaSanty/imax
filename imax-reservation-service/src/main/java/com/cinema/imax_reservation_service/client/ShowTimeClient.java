package com.cinema.imax_reservation_service.client;

import com.cinema.imax_reservation_service.dto.ShowTimeDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(
        name = "imax-showtime-service",
        path = "/showtime-service/api/showtimes",
        fallback = ShowTimeClientFallback.class
)
public interface ShowTimeClient {
    @GetMapping("/{id}")
    ShowTimeDTO getShowTimeById(@PathVariable Long id);

    @PutMapping("/internal/{id}/reserve")
    ShowTimeDTO reserveSeats(@PathVariable Long id, @RequestParam Integer seats);
}