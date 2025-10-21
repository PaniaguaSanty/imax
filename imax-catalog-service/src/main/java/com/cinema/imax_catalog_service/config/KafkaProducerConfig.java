package com.cinema.imax_catalog_service.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();

        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class); // convierte MovieEvent JSON y luego a Bytes.

        // Garantías de entrega: garantizan entrega segura y sin duplicados
        configProps.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true); // → evita que un mensaje se envíe dos veces por error.
        configProps.put(ProducerConfig.ACKS_CONFIG, "all"); // → el broker confirma solo cuando todas las réplicas lo guardaron.
        configProps.put(ProducerConfig.RETRIES_CONFIG, 3); // → reintenta hasta 3 veces si falla.
        configProps.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 1); // → solo un mensaje a la vez en vuelo (para mantener el orden cuando hay reintentos).

        // Optimización de rendimiento
        configProps.put(ProducerConfig.LINGER_MS_CONFIG, 10); // → espera para agrupar varios mensajes en un mismo lote (batch).
        configProps.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384); // → tamaño máximo del lote (16 KB).
        configProps.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy"); // → comprime los mensajes para enviar menos datos.

        // Configuración del serializador JSON
        configProps.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
    // es la clase de alto nivel de Spring para enviar mensajes fácilmente.
}