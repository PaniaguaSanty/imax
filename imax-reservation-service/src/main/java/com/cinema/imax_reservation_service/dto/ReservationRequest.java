package com.cinema.imax_reservation_service.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import org.antlr.v4.runtime.misc.NotNull;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReservationRequest {

    @NotNull
    private Long showTimeId;

    @NotBlank(message = "Client email is required")
    @Email(message = "Invalid email format")
    private String clientEmail;

    @NotBlank(message = "Client name is required")
    @Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters")
    private String clientName;

    @NotNull
    @Min(value = 1, message = "At least 1 seat is required")
    @Max(value = 10, message = "Maximum 10 seats per reservation")
    private Integer numberOfSeats;
}
