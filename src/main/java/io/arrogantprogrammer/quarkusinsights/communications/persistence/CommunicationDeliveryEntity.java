package io.arrogantprogrammer.quarkusinsights.communications.persistence;

import io.arrogantprogrammer.quarkusinsights.communications.domain.CommunicationChannel;
import io.arrogantprogrammer.quarkusinsights.communications.domain.DeliveryStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "communication_delivery",
        indexes = {
                @Index(name = "idx_comm_delivery_due", columnList = "status, next_attempt_at"),
                @Index(name = "idx_comm_delivery_communication", columnList = "communication_id")
        })
public class CommunicationDeliveryEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "communication_id", nullable = false, updatable = false)
    private UUID communicationId;

    @Enumerated(EnumType.STRING)
    @Column(name = "channel", nullable = false)
    private CommunicationChannel channel;

    @Column(name = "destination", nullable = false)
    private String destination;

    @Column(name = "subject", nullable = false)
    private String subject;

    @Column(name = "body", nullable = false, columnDefinition = "TEXT")
    private String body;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private DeliveryStatus status;

    @Column(name = "attempt_count", nullable = false)
    private int attemptCount;

    @Column(name = "next_attempt_at", nullable = false)
    private Instant nextAttemptAt;

    @Column(name = "processing_started_at")
    private Instant processingStartedAt;

    @Column(name = "delivered_at")
    private Instant deliveredAt;

    @Column(name = "last_error", columnDefinition = "TEXT")
    private String lastError;

    public CommunicationDeliveryEntity() {
    }

    public CommunicationDeliveryEntity(UUID id, UUID communicationId, CommunicationChannel channel, String destination,
                                       String subject, String body, DeliveryStatus status, int attemptCount,
                                       Instant nextAttemptAt, Instant processingStartedAt, Instant deliveredAt,
                                       String lastError) {
        this.id = id;
        this.communicationId = communicationId;
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

    public UUID getId() {
        return id;
    }

    public UUID getCommunicationId() {
        return communicationId;
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

    public void setStatus(DeliveryStatus status) {
        this.status = status;
    }

    public void setAttemptCount(int attemptCount) {
        this.attemptCount = attemptCount;
    }

    public void setNextAttemptAt(Instant nextAttemptAt) {
        this.nextAttemptAt = nextAttemptAt;
    }

    public void setProcessingStartedAt(Instant processingStartedAt) {
        this.processingStartedAt = processingStartedAt;
    }

    public void setDeliveredAt(Instant deliveredAt) {
        this.deliveredAt = deliveredAt;
    }

    public void setLastError(String lastError) {
        this.lastError = lastError;
    }
}
