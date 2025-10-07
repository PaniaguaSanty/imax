package com.cinema.imax_reservation_service.service;

import com.cinema.imax_reservation_service.model.Reservation;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.preference.*;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;
import com.mercadopago.resources.preference.Preference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentService {

    @Value("${mercadopago.public-key}")
    private String publicKey;

    @Value("${server.port:8083}")
    private String serverPort;

    @Value("${mercadopago.base-url:http://localhost}")
    private String baseUrl;

    /**
     * Creates a payment preference in MercadoPago and returns the init_point URL
     */
    public String createPaymentPreference(Reservation reservation) {
        try {
            log.info("Creating payment preference for reservation: {}", reservation.getId());

            // Create item for the preference
            PreferenceItemRequest item = PreferenceItemRequest.builder()
                    .id(reservation.getId().toString())
                    .title("Entradas IMAX - " + reservation.getNumberOfSeats() + " asientos")
                    .description("Reserva de entradas para cine IMAX")
                    .pictureUrl("https://www.imax.com/themes/custom/imax/logo.svg")
                    .categoryId("tickets")
                    .quantity(reservation.getNumberOfSeats())
                    .currencyId("ARS")
                    .unitPrice(reservation.getTotalPrice().divide(
                            BigDecimal.valueOf(reservation.getNumberOfSeats()),
                            2,
                            BigDecimal.ROUND_HALF_UP
                    ))
                    .build();

            List<PreferenceItemRequest> items = new ArrayList<>();
            items.add(item);

            // Create back URLs (where MercadoPago redirects the user)
            String fullBaseUrl = baseUrl + ":" + serverPort;
            PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
                    .success(fullBaseUrl + "/api/reservations/payment/success")
                    .failure(fullBaseUrl + "/api/reservations/payment/failure")
                    .pending(fullBaseUrl + "/api/reservations/payment/pending")
                    .build();

            // Payment methods configuration
            PreferencePaymentMethodsRequest paymentMethods = PreferencePaymentMethodsRequest.builder()
                    .excludedPaymentMethods(new ArrayList<>())
                    .excludedPaymentTypes(new ArrayList<>())
                    .installments(12)
                    .defaultInstallments(1)
                    .build();

            // Expiration configuration (10 minutes)
            OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
            OffsetDateTime expirationDate = now.plusMinutes(10);

            // Build the preference request
            PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                    .items(items)
                    .backUrls(backUrls)
                    .autoReturn("approved")
                    .paymentMethods(paymentMethods)
                    .notificationUrl(fullBaseUrl + "/api/reservations/webhook/mercadopago")
                    .externalReference(reservation.getId().toString())
                    .statementDescriptor("CINE IMAX")
                    .expires(true)
                    .expirationDateFrom(now)
                    .expirationDateTo(expirationDate)
                    .build();

            // Create the preference
            PreferenceClient client = new PreferenceClient();
            Preference preference = client.create(preferenceRequest);

            log.info("Payment preference created successfully. ID: {}, Init Point: {}",
                    preference.getId(), preference.getInitPoint());

            return preference.getInitPoint();

        } catch (MPApiException e) {
            log.error("MercadoPago API Error: Status={}, Content={}",
                    e.getApiResponse().getStatusCode(),
                    e.getApiResponse().getContent(),
                    e);
            throw new RuntimeException("Error creating payment preference: " + e.getMessage(), e);
        } catch (MPException e) {
            log.error("MercadoPago SDK Error: {}", e.getMessage(), e);
            throw new RuntimeException("Error with MercadoPago SDK: " + e.getMessage(), e);
        }
    }

    /**
     * Verifies if a payment was approved in MercadoPago
     */
    public boolean verifyPayment(String paymentId) {
        try {
            log.info("Verifying payment: {}", paymentId);

            PaymentClient client = new PaymentClient();
            Payment payment = client.get(Long.parseLong(paymentId));

            log.info("Payment status: {}, Status detail: {}",
                    payment.getStatus(),
                    payment.getStatusDetail());

            // Payment is approved if status is "approved"
            boolean isApproved = "approved".equals(payment.getStatus());

            if (isApproved) {
                log.info("Payment {} verified successfully", paymentId);
            } else {
                log.warn("Payment {} not approved. Status: {}", paymentId, payment.getStatus());
            }

            return isApproved;

        } catch (MPApiException e) {
            log.error("MercadoPago API Error verifying payment: Status={}, Content={}",
                    e.getApiResponse().getStatusCode(),
                    e.getApiResponse().getContent(),
                    e);
            return false;
        } catch (MPException e) {
            log.error("MercadoPago SDK Error verifying payment: {}", e.getMessage(), e);
            return false;
        } catch (NumberFormatException e) {
            log.error("Invalid payment ID format: {}", paymentId);
            return false;
        }
    }

    /**
     * Gets payment details from MercadoPago
     */
    public Payment getPaymentDetails(Long paymentId) throws MPException, MPApiException {
        log.info("Getting payment details for: {}", paymentId);
        PaymentClient client = new PaymentClient();
        return client.get(paymentId);
    }

    /**
     * Checks if a payment exists and is valid
     */
    public boolean paymentExists(String paymentId) {
        try {
            PaymentClient client = new PaymentClient();
            Payment payment = client.get(Long.parseLong(paymentId));
            return payment != null;
        } catch (Exception e) {
            log.error("Error checking if payment exists: {}", paymentId, e);
            return false;
        }
    }
}