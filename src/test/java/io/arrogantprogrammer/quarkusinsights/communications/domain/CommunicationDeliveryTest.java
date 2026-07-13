package io.arrogantprogrammer.quarkusinsights.communications.domain;

import io.arrogantprogrammer.quarkusinsights.communications.domain.aggregates.CommunicationDelivery;
import io.arrogantprogrammer.quarkusinsights.communications.domain.valueobjects.EmailMessage;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CommunicationDeliveryTest {

    private static CommunicationDelivery pendingDelivery() {
        return CommunicationDelivery.email("jane@example.com", "Subject", "Body", Instant.now());
    }

    @Test
    void claimTransitionsToProcessingAndReturnsMessage() {
        CommunicationDelivery delivery = pendingDelivery();
        EmailMessage message = delivery.claimForDelivery(Instant.now());
        assertEquals(DeliveryStatus.PROCESSING, delivery.getStatus());
        assertEquals("jane@example.com", message.to());
        assertEquals("Subject", message.subject());
        assertEquals("Body", message.body());
        assertEquals(delivery.getId(), message.deliveryId());
    }

    @Test
    void successTransitionsToDelivered() {
        CommunicationDelivery delivery = pendingDelivery();
        delivery.claimForDelivery(Instant.now());
        delivery.markDelivered(Instant.now());
        assertEquals(DeliveryStatus.DELIVERED, delivery.getStatus());
    }

    @Test
    void failureSchedulesRetryAndIncrementsAttempts() {
        CommunicationDelivery delivery = pendingDelivery();
        delivery.claimForDelivery(Instant.now());
        Instant nextAttempt = Instant.now().plusSeconds(5);
        delivery.scheduleRetry("SMTP down", nextAttempt, 5);
        assertEquals(DeliveryStatus.RETRY_SCHEDULED, delivery.getStatus());
        assertEquals(1, delivery.getAttemptCount());
        assertEquals(nextAttempt, delivery.getNextAttemptAt());
    }

    @Test
    void reachingMaxAttemptsMarksPermanentlyFailed() {
        CommunicationDelivery delivery = pendingDelivery();
        delivery.claimForDelivery(Instant.now());
        delivery.scheduleRetry("fail", Instant.now(), 1);
        assertEquals(DeliveryStatus.PERMANENTLY_FAILED, delivery.getStatus());
    }

    @Test
    void claimingDeliveredDeliveryFails() {
        CommunicationDelivery delivery = pendingDelivery();
        delivery.claimForDelivery(Instant.now());
        delivery.markDelivered(Instant.now());
        assertThrows(IllegalStateException.class, () -> delivery.claimForDelivery(Instant.now()));
    }

    @Test
    void markingDeliveredWhenNotProcessingFails() {
        CommunicationDelivery delivery = pendingDelivery();
        assertThrows(IllegalStateException.class, () -> delivery.markDelivered(Instant.now()));
    }

    @Test
    void retryingPermanentlyFailedDeliveryFails() {
        CommunicationDelivery delivery = pendingDelivery();
        delivery.claimForDelivery(Instant.now());
        delivery.scheduleRetry("fail", Instant.now(), 1);
        assertThrows(IllegalStateException.class, () -> delivery.scheduleRetry("again", Instant.now(), 1));
    }
}
