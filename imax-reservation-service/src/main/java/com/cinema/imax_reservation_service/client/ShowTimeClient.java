package com.cinema.imax_reservation_service.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(
        name = "imax-reservation-service", // Name registered in Eureka
        path = "/reservation-service/api/reservations", // Routes prefix
        fallback = ReservationClientFallback.class
)
public interface ShowTimeClient {
}
