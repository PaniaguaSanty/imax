package com.cinema.imax_reservation_service.repository;

import com.cinema.imax_reservation_service.model.Reservation;
import com.cinema.imax_reservation_service.model.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, UUID> {

    List<Reservation> findByClientEmail(String clientEmail);

    List<Reservation> findByShowTimeId(Long showTimeId);

    List<Reservation> findByStatus(ReservationStatus status);

    List<Reservation> findByStatusAndExpiresAtBefore(ReservationStatus status, LocalDateTime dateTime);

    List<Reservation> findByQrCode(String qrCode);
}