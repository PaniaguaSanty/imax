package com.cinema.imax_reservation_service.model;


import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "reservations")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private Long showTimeId;
    private String clientEmail;
    private String clientName;
    private Integer numberOfSeats;
    private BigDecimal totalPrice;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    private String qrCode; // unique UUID
    private String paymentId; //ID of MercadoPago

    private LocalDateTime createdAt;
    private LocalDateTime expiresAt; //10 minutes to complete payment.
    private LocalDateTime confirmedAt;

}
