package com.cinema.imax_catalog_service.service;

import com.cinema.imax_catalog_service.dto.event.MovieEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class MovieEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${spring.kafka.topics.movie-events}")
    private String movieEventsTopic;

    /**
     * Publica un evento de sincronización completa del catálogo
     */
    public void publishSyncCompletedEvent(MovieEvent.SyncEventData syncData) {

        MovieEvent event = MovieEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType("SYNC_COMPLETED")
                .timestamp(LocalDateTime.now())
                .data(syncData)
                .build();

        // Esto asegura que todos los eventos de sync vayan a la misma partición
        String key = "catalog-sync";

        log.info("📤 Publicando evento de sincronización | Total películas: {} | Agregadas: {} | Actualizadas: {} | Removidas: {}",
                syncData.getTotalMoviesInCatalog(),
                syncData.getMoviesAdded(),
                syncData.getMoviesUpdated(),
                syncData.getMoviesRemoved());

        CompletableFuture<SendResult<String, Object>> future =
                kafkaTemplate.send(movieEventsTopic, key, event);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("✅ Evento de sincronización publicado exitosamente | EventId: {} | Offset: {} | Partition: {}",
                        event.getEventId(),
                        result.getRecordMetadata().offset(),
                        result.getRecordMetadata().partition());
            } else {
                log.error("❌ Error al publicar evento de sincronización | EventId: {} | Error: {}",
                        event.getEventId(),
                        ex.getMessage(), ex);
            }
        });
    }

    /**
     * Publica un evento para una película individual (opcional)
     */
    public void publishMovieEvent(String eventType, MovieEvent.MovieData movieData) {

        // Crear un SyncEventData con una sola película
        MovieEvent.SyncEventData syncData = MovieEvent.SyncEventData.builder()
                .totalMoviesInCatalog(1)
                .moviesAdded(eventType.equals("MOVIE_ADDED") ? 1 : 0)
                .moviesUpdated(eventType.equals("MOVIE_UPDATED") ? 1 : 0)
                .moviesRemoved(eventType.equals("MOVIE_REMOVED") ? 1 : 0)
                .syncSource("MANUAL")
                .movies(List.of(movieData))
                .build();

        MovieEvent event = MovieEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType(eventType)
                .timestamp(LocalDateTime.now())
                .data(syncData)
                .build();

        String key = movieData.getId().toString();

        log.info("📤 Publicando evento: {} para película ID: {} - {}",
                eventType, movieData.getId(), movieData.getTitle());

        CompletableFuture<SendResult<String, Object>> future =
                kafkaTemplate.send(movieEventsTopic, key, event);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("✅ Evento publicado | EventId: {} | Offset: {}",
                        event.getEventId(),
                        result.getRecordMetadata().offset());
            } else {
                log.error("❌ Error al publicar evento | EventId: {} | Error: {}",
                        event.getEventId(),
                        ex.getMessage(), ex);
            }
        });
    }
}