package com.cinema.imax_reservation_service.service;

import com.cinema.imax_reservation_service.client.ShowTimeClient;
import com.cinema.imax_reservation_service.dto.ReservationRequest;
import com.cinema.imax_reservation_service.dto.ShowTimeDTO;
import com.cinema.imax_reservation_service.model.Reservation;
import com.cinema.imax_reservation_service.model.ReservationStatus;
import com.cinema.imax_reservation_service.repository.ReservationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ShowTimeClient showTimeClient;
    private final PaymentService paymentService;

    @Transactional
    public Reservation createReservation(ReservationRequest request) {
        log.info("Creating reservation for showTime: {}", request.getShowTimeId());

        // 1. Verify showtime exists and has available seats
        ShowTimeDTO showTime = showTimeClient.getShowTimeById(request.getShowTimeId());
        if (showTime == null) {
            throw new IllegalArgumentException("ShowTime not found: " + request.getShowTimeId());
        }

        if (showTime.getAvailableSeats() < request.getNumberOfSeats()) {
            throw new IllegalStateException("Not enough available seats. Available: " +
                                            showTime.getAvailableSeats() + ", Requested: " + request.getNumberOfSeats());
        }

        // 2. Calculate total price
        BigDecimal totalPrice = showTime.getPrice()
                .multiply(BigDecimal.valueOf(request.getNumberOfSeats()));

        // 3. Create reservation in PENDING status
        Reservation reservation = Reservation.builder()
                .showTimeId(request.getShowTimeId())
                .clientEmail(request.getClientEmail())
                .clientName(request.getClientName())
                .numberOfSeats(request.getNumberOfSeats())
                .totalPrice(totalPrice)
                .status(ReservationStatus.PENDING)
                .qrCode(UUID.randomUUID().toString())
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(10))
                .build();

        // Save first to get the ID
        reservation = reservationRepository.save(reservation);
        log.info("Reservation created with ID: {}", reservation.getId());

        // 4. Reserve seats in ShowTime service
        try {
            showTimeClient.reserveSeats(request.getShowTimeId(), request.getNumberOfSeats());
            log.info("Seats reserved in ShowTime service");
        } catch (Exception e) {
            log.error("Error reserving seats, rolling back", e);
            // Delete the reservation since we couldn't reserve seats
            reservationRepository.delete(reservation);
            throw new IllegalStateException("Error reserving seats: " + e.getMessage());
        }

        // 5. Create payment preference in MercadoPago
        try {
            String paymentUrl = paymentService.createPaymentPreference(reservation);
            reservation.setPaymentId(paymentUrl); // Store the payment URL
            reservation = reservationRepository.save(reservation);
            log.info("Payment URL created: {}", paymentUrl);
        } catch (Exception e) {
            log.error("Error creating payment preference", e);
            // Continue anyway, but log the error
            // The reservation exists, payment can be created later if needed
        }

        return reservation;
    }

    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    public Reservation getReservationById(UUID id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found: " + id));
    }

    public List<Reservation> getReservationsByEmail(String email) {
        return reservationRepository.findByClientEmail(email);
    }

    public List<Reservation> getReservationsByShowTime(Long showTimeId) {
        return reservationRepository.findByShowTimeId(showTimeId);
    }

    public ShowTimeDTO getShowTimeById(Long id) {
        return showTimeClient.getShowTimeById(id);
    }

    public ShowTimeDTO reserveSeatsInShowTime(Long id, Integer seats) {
        return showTimeClient.reserveSeats(id, seats);
    }

    @Transactional
    public Reservation confirmPayment(UUID reservationId, String paymentId) {
        log.info("Confirming payment for reservation: {}", reservationId);

        Reservation reservation = getReservationById(reservationId);

        if (reservation.getStatus() == ReservationStatus.CONFIRMED) {
            log.info("Reservation already confirmed: {}", reservationId);
            return reservation;
        }

        if (reservation.getStatus() != ReservationStatus.PENDING) {
            throw new IllegalStateException("Reservation is not in PENDING status. Current status: " +
                                            reservation.getStatus());
        }

        if (LocalDateTime.now().isAfter(reservation.getExpiresAt())) {
            reservation.setStatus(ReservationStatus.EXPIRED);
            reservationRepository.save(reservation);
            throw new IllegalStateException("Reservation has expired");
        }

        // Verify payment with MercadoPago
        boolean paymentConfirmed = paymentService.verifyPayment(paymentId);

        if (paymentConfirmed) {
            reservation.setStatus(ReservationStatus.CONFIRMED);
            reservation.setPaymentId(paymentId);
            reservation.setConfirmedAt(LocalDateTime.now());
            reservation = reservationRepository.save(reservation);
            log.info("Reservation confirmed: {}", reservationId);
        } else {
            throw new IllegalStateException("Payment verification failed for payment: " + paymentId);
        }

        return reservation;
    }

    @Transactional
    public Reservation cancelReservation(UUID reservationId) {
        log.info("Cancelling reservation: {}", reservationId);

        Reservation reservation = getReservationById(reservationId);

        if (reservation.getStatus() == ReservationStatus.CONFIRMED) {
            throw new IllegalStateException("Cannot cancel confirmed reservation");
        }

        if (reservation.getStatus() == ReservationStatus.CANCELLED ||
            reservation.getStatus() == ReservationStatus.EXPIRED) {
            log.info("Reservation already cancelled/expired: {}", reservationId);
            return reservation;
        }

        reservation.setStatus(ReservationStatus.CANCELLED);
        reservationRepository.save(reservation);

        // Return seats to ShowTime service (negative number to add back)
        try {
            showTimeClient.reserveSeats(reservation.getShowTimeId(), -reservation.getNumberOfSeats());
            log.info("Seats returned to ShowTime service");
        } catch (Exception e) {
            log.error("Error returning seats to ShowTime service", e);
            // Continue anyway, the reservation is still cancelled
        }

        log.info("Reservation cancelled: {}", reservationId);
        return reservation;
    }

    @Transactional
    public void expireOldReservations() {
        log.info("Checking for expired reservations...");

        List<Reservation> pendingReservations = reservationRepository
                .findByStatusAndExpiresAtBefore(ReservationStatus.PENDING, LocalDateTime.now());

        if (pendingReservations.isEmpty()) {
            log.debug("No expired reservations found");
            return;
        }

        log.info("Found {} expired reservations", pendingReservations.size());

        for (Reservation reservation : pendingReservations) {
            try {
                reservation.setStatus(ReservationStatus.EXPIRED);
                reservationRepository.save(reservation);

                // Return seats to ShowTime service
                showTimeClient.reserveSeats(reservation.getShowTimeId(), -reservation.getNumberOfSeats());

                log.info("Expired reservation: {} - Seats returned", reservation.getId());
            } catch (Exception e) {
                log.error("Error expiring reservation: {}", reservation.getId(), e);
            }
        }
    }
}