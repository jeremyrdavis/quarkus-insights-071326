package io.arrogantprogrammer.quarkusinsights.communications.domain.aggregates;

import io.arrogantprogrammer.quarkusinsights.communications.domain.CommunicationChannel;
import io.arrogantprogrammer.quarkusinsights.communications.domain.DeliveryStatus;
import io.arrogantprogrammer.quarkusinsights.communications.domain.valueobjects.EmailMessage;

import java.time.Instant;
import java.util.UUID;

/**
 * A single attempt-channel for a {@link Communication}. Owns delivery state and
 * the retry lifecycle. State changes are intention-revealing methods that reject
 * invalid transitions — there are no setters.
 */
public class CommunicationDelivery {

    private UUID id;
    private CommunicationChannel channel;
    private String destination;
    private String subject;
    private String body;
    private DeliveryStatus status;
    private int attemptCount;
    private Instant nextAttemptAt;
    private Instant processingStartedAt;
    private Instant deliveredAt;
    private String lastError;

    public CommunicationDelivery() {
    }

    public CommunicationDelivery(UUID id, CommunicationChannel channel, String destination, String subject,
                                 String body, DeliveryStatus status, int attemptCount, Instant nextAttemptAt,
                                 Instant processingStartedAt, Instant deliveredAt, String lastError) {
        this.id = id;
        this.channel = channel;
        this.destination = destination;
        this.subject = subject;
        this.body = body;
        this.status = status;
        this.attemptCount = attemptCount;
        this.nextAttemptAt = nextAttemptAt;
        this.processingStartedAt = processingStartedAt;
        this.deliveredAt = deliveredAt;
        this.lastError = lastError;
    }

    /** A fresh, pending email delivery due immediately at {@code createdAt}. */
    public static CommunicationDelivery email(String destination, String subject, String body, Instant createdAt) {
        return new CommunicationDelivery(UUID.randomUUID(), CommunicationChannel.EMAIL, destination, subject,
                body, DeliveryStatus.PENDING, 0, createdAt, null, null, null);
    }

    /**
     * Claim this delivery for a send attempt, transitioning it to PROCESSING and
     * returning the message to hand to the channel adapter. A stale PROCESSING
     * delivery may be reclaimed (at-least-once). Delivered or permanently-failed
     * deliveries cannot be claimed.
     */
    public EmailMessage claimForDelivery(Instant now) {
        if (status == DeliveryStatus.DELIVERED) {
            throw new IllegalStateException("Delivery " + id + " is already delivered");
        }
        if (status == DeliveryStatus.PERMANENTLY_FAILED) {
            throw new IllegalStateException("Delivery " + id + " is permanently failed");
        }
        this.status = DeliveryStatus.PROCESSING;
        this.processingStartedAt = now;
        return new EmailMessage(id, destination, subject, body);
    }

    public void markDelivered(Instant deliveredAt) {
        if (status != DeliveryStatus.PROCESSING) {
            throw new IllegalStateException("Delivery " + id + " must be PROCESSING to be marked delivered");
        }
        this.status = DeliveryStatus.DELIVERED;
        this.deliveredAt = deliveredAt;
        this.lastError = null;
    }

    /**
     * Record a failed send: increment attempts, store a sanitized error, and
     * either schedule a retry or give up permanently once the maximum number of
     * attempts is reached.
     */
    public void scheduleRetry(String error, Instant nextAttemptAt, int maxAttempts) {
        if (status == DeliveryStatus.PERMANENTLY_FAILED) {
            throw new IllegalStateException("Delivery " + id + " is permanently failed; requeue explicitly");
        }
        this.attemptCount += 1;
        this.lastError = error;
        this.nextAttemptAt = nextAttemptAt;
        this.processingStartedAt = null;
        this.status = this.attemptCount >= maxAttempts
                ? DeliveryStatus.PERMANENTLY_FAILED
                : DeliveryStatus.RETRY_SCHEDULED;
    }

    public UUID getId() {
        return id;
    }

    public CommunicationChannel getChannel() {
        return channel;
    }

    public String getDestination() {
        return destination;
    }

    public String getSubject() {
        return subject;
    }

    public String getBody() {
        return body;
    }

    public DeliveryStatus getStatus() {
        return status;
    }

    public int getAttemptCount() {
        return attemptCount;
    }

    public Instant getNextAttemptAt() {
        return nextAttemptAt;
    }

    public Instant getProcessingStartedAt() {
        return processingStartedAt;
    }

    public Instant getDeliveredAt() {
        return deliveredAt;
    }

    public String getLastError() {
        return lastError;
    }
}
