package com.cinema.imax_reservation_service.controller;

import com.cinema.imax_reservation_service.dto.ReservationRequest;
import com.cinema.imax_reservation_service.model.Reservation;
import com.cinema.imax_reservation_service.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("reservation-service/api/reservations")
@RequiredArgsConstructor
@Slf4j
public class ReservationController {

    private final ReservationService reservationService;

    /**
     * Crear una nueva reserva
     * POST http://localhost:8083/reservation-service/api/reservations
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createReservation(@Valid @RequestBody ReservationRequest request) {
        try {
            Reservation reservation = reservationService.createReservation(request);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("reservationId", reservation.getId());
            response.put("status", reservation.getStatus());
            response.put("totalPrice", reservation.getTotalPrice());
            response.put("qrCode", reservation.getQrCode());
            response.put("expiresAt", reservation.getExpiresAt());

            // URL de MercadoPago para realizar el pago
            if (reservation.getPaymentId() != null) {
                response.put("paymentUrl", reservation.getPaymentId());
                response.put("message", "Reserva creada exitosamente. Complete el pago en la URL proporcionada.");
            } else {
                response.put("message", "Reserva creada pero hubo un error al generar el link de pago.");
            }

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IllegalArgumentException e) {
            log.error("Error de validación: {}", e.getMessage());
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);

        } catch (IllegalStateException e) {
            log.error("Error de estado: {}", e.getMessage());
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(error);

        } catch (Exception e) {
            log.error("Error inesperado al crear reserva", e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "Error interno del servidor");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Obtener todas las reservas
     * GET http://localhost:8083/api/reservations
     */
    @GetMapping
    public ResponseEntity<List<Reservation>> getAllReservations() {
        return ResponseEntity.ok(reservationService.getAllReservations());
    }

    /**
     * Obtener reserva por ID
     * GET http://localhost:8083/api/reservations/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Reservation> getReservationById(@PathVariable UUID id) {
        try {
            return ResponseEntity.ok(reservationService.getReservationById(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Obtener reservas por email del cliente
     * GET http://localhost:8083/api/reservations/email/{email}
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<List<Reservation>> getReservationsByEmail(@PathVariable String email) {
        return ResponseEntity.ok(reservationService.getReservationsByEmail(email));
    }

    /**
     * Obtener reservas por función de cine
     * GET http://localhost:8083/api/reservations/showtime/{showTimeId}
     */
    @GetMapping("/showtime/{showTimeId}")
    public ResponseEntity<List<Reservation>> getReservationsByShowTime(@PathVariable Long showTimeId) {
        return ResponseEntity.ok(reservationService.getReservationsByShowTime(showTimeId));
    }

    /**
     * Confirmar pago (webhook o llamada manual)
     * POST http://localhost:8083/api/reservations/{reservationId}/confirm-payment
     */
    @PostMapping("/{reservationId}/confirm-payment")
    public ResponseEntity<Map<String, Object>> confirmPayment(
            @PathVariable UUID reservationId,
            @RequestParam String paymentId) {
        try {
            Reservation reservation = reservationService.confirmPayment(reservationId, paymentId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("reservationId", reservation.getId());
            response.put("status", reservation.getStatus());
            response.put("confirmedAt", reservation.getConfirmedAt());
            response.put("message", "Pago confirmado exitosamente");

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "Reserva no encontrada");
            return ResponseEntity.notFound().build();

        } catch (IllegalStateException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(error);

        } catch (Exception e) {
            log.error("Error confirmando pago", e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "Error al confirmar el pago");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Webhook de MercadoPago (recibe notificaciones de pago)
     * POST http://localhost:8083/api/reservations/webhook/mercadopago
     */
    @PostMapping("/webhook/mercadopago")
    public ResponseEntity<String> webhookMercadoPago(
            @RequestParam(required = false) String topic,
            @RequestParam(required = false) String id,
            @RequestBody(required = false) Map<String, Object> payload) {

        log.info("Webhook MercadoPago recibido - Topic: {}, ID: {}, Payload: {}", topic, id, payload);

        try {
            if ("payment".equals(topic) && id != null) {
                // Aquí procesarías el pago automáticamente
                // Por ahora solo logueamos
                log.info("Pago recibido con ID: {}", id);
            }
            return ResponseEntity.ok("Webhook procesado");
        } catch (Exception e) {
            log.error("Error procesando webhook", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error");
        }
    }

    /**
     * URLs de retorno de MercadoPago
     */
    @GetMapping("/payment/success")
    public ResponseEntity<Map<String, Object>> paymentSuccess(
            @RequestParam(required = false) String payment_id,
            @RequestParam(required = false) String external_reference,
            @RequestParam(required = false) String status) {

        log.info("Pago exitoso - PaymentId: {}, Reference: {}, Status: {}",
                payment_id, external_reference, status);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "¡Pago exitoso!");
        response.put("paymentId", payment_id);
        response.put("reservationId", external_reference);
        response.put("status", status);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/payment/failure")
    public ResponseEntity<Map<String, Object>> paymentFailure(
            @RequestParam(required = false) String payment_id,
            @RequestParam(required = false) String external_reference) {

        log.warn("Pago fallido - PaymentId: {}, Reference: {}", payment_id, external_reference);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Pago fallido");
        response.put("paymentId", payment_id);
        response.put("reservationId", external_reference);

        return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED).body(response);
    }

    @GetMapping("/payment/pending")
    public ResponseEntity<Map<String, Object>> paymentPending(
            @RequestParam(required = false) String payment_id,
            @RequestParam(required = false) String external_reference) {

        log.info("Pago pendiente - PaymentId: {}, Reference: {}", payment_id, external_reference);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Pago pendiente");
        response.put("paymentId", payment_id);
        response.put("reservationId", external_reference);

        return ResponseEntity.ok(response);
    }
}