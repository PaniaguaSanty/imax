package com.cinema.imax_notification_service.service;

import com.cinema.imax_notification_service.repository.MovieEventLogRepository;
import com.cinema.imax_notification_service.dto.event.MovieEvent;
import com.cinema.imax_notification_service.model.MovieEventLog;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final MovieEventLogRepository eventLogRepository;
    private final ObjectMapper objectMapper;

    /**
     * Guarda el evento en la base de datos y lo procesa
     */
    @Transactional
    public void processMovieEvent(MovieEvent event, int partition, long offset) {
        // Verificar si ya fue procesado (idempotencia)
        if (eventLogRepository.existsByEventId(event.getEventId())) {
            log.warn("⚠️ Evento ya procesado anteriormente: {}", event.getEventId());
            return;
        }

        try {
            // Guardar el evento en BD
            MovieEventLog eventLog = saveEventLog(event, partition, offset);
            log.info("💾 Evento guardado en BD con ID: {}", eventLog.getId());

            // Procesar según el tipo
            processEventByType(event);

        } catch (Exception e) {
            log.error("❌ Error al procesar evento: {}", event.getEventId(), e);
            saveFailedEventLog(event, partition, offset, e.getMessage());
            throw e; // Re-lanzar para que Kafka no haga acknowledge
        }
    }

    /**
     * Guarda el evento exitosamente
     */
    private MovieEventLog saveEventLog(MovieEvent event, int partition, long offset) {
        MovieEventLog.MovieEventLogBuilder builder = MovieEventLog.builder()
                .eventId(event.getEventId())
                .eventType(event.getEventType())
                .eventTimestamp(event.getTimestamp())
                .kafkaPartition(partition)
                .kafkaOffset(offset)
                .status("SUCCESS");

        // Agregar datos específicos del evento
        if (event.getData() != null) {
            MovieEvent.SyncEventData data = event.getData();
            builder.totalMovies(data.getTotalMoviesInCatalog())
                    .moviesAdded(data.getMoviesAdded())
                    .moviesUpdated(data.getMoviesUpdated())
                    .moviesRemoved(data.getMoviesRemoved())
                    .syncSource(data.getSyncSource());
        }

        // Guardar el JSON completo del evento (opcional)
        try {
            String eventJson = objectMapper.writeValueAsString(event);
            builder.eventData(eventJson);
        } catch (JsonProcessingException e) {
            log.warn("⚠️ No se pudo serializar el evento a JSON: {}", e.getMessage());
        }

        return eventLogRepository.save(builder.build());
    }

    /**
     * Guarda un evento que falló
     */
    private void saveFailedEventLog(MovieEvent event, int partition, long offset, String error) {
        try {
            MovieEventLog failedLog = MovieEventLog.builder()
                    .eventId(event.getEventId())
                    .eventType(event.getEventType())
                    .eventTimestamp(event.getTimestamp())
                    .kafkaPartition(partition)
                    .kafkaOffset(offset)
                    .status("FAILED")
                    .errorMessage(error)
                    .build();

            eventLogRepository.save(failedLog);
        } catch (Exception e) {
            log.error("❌ Error crítico: no se pudo guardar evento fallido", e);
        }
    }

    /**
     * Procesa eventos según su tipo
     */
    private void processEventByType(MovieEvent event) {
        String eventType = event.getEventType();

        switch (eventType) {
            case "SYNC_COMPLETED":
                handleSyncCompleted(event);
                break;
            case "MOVIE_ADDED":
                handleMovieAdded(event);
                break;
            case "MOVIE_UPDATED":
                handleMovieUpdated(event);
                break;
            case "MOVIE_REMOVED":
                handleMovieRemoved(event);
                break;
            default:
                log.warn("⚠️ Tipo de evento desconocido: {}", eventType);
        }
    }

    private void handleSyncCompleted(MovieEvent event) {
        MovieEvent.SyncEventData data = event.getData();

        log.info("🔄 SYNC COMPLETADO");
        log.info("   📦 Total en catálogo: {}", data.getTotalMoviesInCatalog());
        log.info("   ➕ Películas agregadas: {}", data.getMoviesAdded());
        log.info("   🔄 Películas actualizadas: {}", data.getMoviesUpdated());
        log.info("   ➖ Películas removidas: {}", data.getMoviesRemoved());
        log.info("   📡 Fuente: {}", data.getSyncSource());

        if (data.getMovies() != null && !data.getMovies().isEmpty()) {
            log.info("   🎬 Primeras películas:");
            data.getMovies().stream()
                    .limit(5)
                    .forEach(movie -> log.info("      - {} (ID: {})", movie.getTitle(), movie.getId()));
        }
    }

    private void handleMovieAdded(MovieEvent event) {
        MovieEvent.SyncEventData data = event.getData();

        if (data.getMovies() != null && !data.getMovies().isEmpty()) {
            MovieEvent.MovieData movie = data.getMovies().get(0);

            log.info("➕ NUEVA PELÍCULA AGREGADA");
            log.info("   🎬 Título: {}", movie.getTitle());
            log.info("   🆔 ID: {}", movie.getId());
            log.info("   📅 Fecha: {}", movie.getReleaseDate());
        }
    }

    private void handleMovieUpdated(MovieEvent event) {
        MovieEvent.SyncEventData data = event.getData();

        if (data.getMovies() != null && !data.getMovies().isEmpty()) {
            MovieEvent.MovieData movie = data.getMovies().get(0);

            log.info("🔄 PELÍCULA ACTUALIZADA");
            log.info("   🎬 Título: {}", movie.getTitle());
            log.info("   🆔 ID: {}", movie.getId());
        }
    }

    private void handleMovieRemoved(MovieEvent event) {
        MovieEvent.SyncEventData data = event.getData();

        if (data.getMovies() != null && !data.getMovies().isEmpty()) {
            MovieEvent.MovieData movie = data.getMovies().get(0);

            log.info("➖ PELÍCULA REMOVIDA");
            log.info("   🎬 Título: {}", movie.getTitle());
            log.info("   🆔 ID: {}", movie.getId());
        }
    }
}