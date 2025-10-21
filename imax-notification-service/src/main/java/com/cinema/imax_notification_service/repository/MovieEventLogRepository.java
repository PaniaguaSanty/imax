package com.cinema.imax_notification_service.repository;

import com.cinema.imax_notification_service.model.MovieEventLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MovieEventLogRepository extends JpaRepository<MovieEventLog, Long> {

    // Verificar si un evento ya fue procesado
    boolean existsByEventId(String eventId);

    // Buscar por eventId
    Optional<MovieEventLog> findByEventId(String eventId);

    // Obtener eventos por tipo
    List<MovieEventLog> findByEventType(String eventType);

    // Obtener eventos en un rango de fechas
    List<MovieEventLog> findByEventTimestampBetween(LocalDateTime start, LocalDateTime end);

    // Obtener los últimos N eventos
    List<MovieEventLog> findTop10ByOrderByProcessedAtDesc();

    // Contar eventos por tipo
    @Query("SELECT e.eventType, COUNT(e) FROM MovieEventLog e GROUP BY e.eventType")
    List<Object[]> countEventsByType();

    // Obtener eventos fallidos
    List<MovieEventLog> findByStatus(String status);
}