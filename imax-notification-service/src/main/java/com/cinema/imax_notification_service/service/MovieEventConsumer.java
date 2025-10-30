package com.cinema.imax_notification_service.service;

import com.cinema.imax_notification_service.dto.event.MovieEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MovieEventConsumer {

    private final NotificationService notificationService;

    /**
     * Escucha eventos del topic movie-events-topic
     *
     * @param event Evento recibido del Producer
     * @param key Key del mensaje
     * @param partition Partición de donde vino el mensaje
     * @param offset Offset del mensaje
     * @param acknowledgment Para hacer commit manual
     */
    @KafkaListener(
            topics = "${spring.kafka.topics.movie-events}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeMovieEvent(
            @Payload MovieEvent event,
            @Header(KafkaHeaders.RECEIVED_KEY) String key,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            Acknowledgment acknowledgment
    ) {
        try {
            log.info("Mensaje recibido | EventId: {} | Key: {} | Partition: {} | Offset: {}",
                    event.getEventId(), key, partition, offset);

            log.info("Tipo de evento: {} | Timestamp: {}",
                    event.getEventType(), event.getTimestamp());

            // Procesar el evento según su tipo
            notificationService.processMovieEvent(event, partition, offset);

            // Commit manual - confirma que el mensaje fue procesado correctamente
            acknowledgment.acknowledge();

            log.info("Mensaje procesado y commiteado exitosamente | Offset: {}", offset);

        } catch (Exception e) {
            log.error("Error al procesar mensaje | EventId: {} | Offset: {} | Error: {}",
                    event.getEventId(), offset, e.getMessage(), e);

            // NO hacemos acknowledge() - el mensaje se volverá a procesar
            // En producción, aquí podrías implementar:
            // 1. Dead Letter Queue (DLQ)
            // 2. Reintentos con backoff
            // 3. Alertas
        }
    }
}