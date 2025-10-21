package com.cinema.imax_notification_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Table(name = "movie_event_logs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieEventLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_id", nullable = false, unique = true)
    private String eventId;

    @Column(name = "event_type", nullable = false)
    private String eventType;

    @Column(name = "event_timestamp", nullable = false)
    private LocalDateTime eventTimestamp;

    @Column(name = "total_movies")
    private Integer totalMovies;

    @Column(name = "movies_added")
    private Integer moviesAdded;

    @Column(name = "movies_updated")
    private Integer moviesUpdated;

    @Column(name = "movies_removed")
    private Integer moviesRemoved;

    @Column(name = "sync_source")
    private String syncSource;

    @Column(name = "processed_at", nullable = false)
    @CreatedDate
    private LocalDateTime processedAt;

    @Column(name = "kafka_partition")
    private Integer kafkaPartition;

    @Column(name = "kafka_offset")
    private Long kafkaOffset;

    @Column(name = "status")
    private String status; // SUCCESS, FAILED

    @Column(name = "error_message", length = 1000)
    private String errorMessage;

    // Opcional: guardar el JSON completo del evento
    @Column(name = "event_data", columnDefinition = "TEXT")
    private String eventData;

    @PrePersist
    protected void onCreate() {
        processedAt = LocalDateTime.now();
        if (status == null) {
            status = "SUCCESS";
        }
    }
}