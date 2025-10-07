package com.cinema.imax_reservation_service.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReservationRequest {

    private Long showTimeId;
    private String clientEmail;
    private String clientName;
    private Integer numberOfSeats;
}
