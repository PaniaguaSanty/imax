package com.cinema.imax_reservation_service.client;

import com.cinema.imax_reservation_service.dto.ShowTimeDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "imax-showTime-service", // Name registered in Eureka
        path = "/showTime-service/api/showtimes", // Routes prefix
        fallback = ShowTimeClientFallback.class
)
public interface ShowTimeClient {

    @GetMapping("/{id}")
    ShowTimeDTO getShowTimeById(@PathVariable Long id);

    @PatchMapping("/internal/{id}/reserve")
    ShowTimeDTO reserveSeats(@PathVariable Long id, @RequestParam Integer seats);
}
