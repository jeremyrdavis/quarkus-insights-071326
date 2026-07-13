package io.arrogantprogrammer.quarkusinsights.cfp.persistence.outbox;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

/**
 * Durable CFP outbox row. Written in the same transaction as the aggregate
 * update, then drained by the scheduled publisher. The stored {@code eventType}
 * is a stable wire identifier — never a Java class name — so payloads remain
 * deserializable across refactors.
 */
@Entity
@Table(
        name = "cfp_outbox_event",
        indexes = {
                @Index(name = "idx_cfp_outbox_due", columnList = "status, next_attempt_at"),
                @Index(name = "idx_cfp_outbox_aggregate", columnList = "aggregate_id")
        })
public class CfpOutboxEventEntity {

    @Id
    @Column(name = "event_id", nullable = false, updatable = false)
    private UUID eventId;

    @Column(name = "event_type", nullable = false)
    private String eventType;

    @Column(name = "event_version", nullable = false)
    private int eventVersion;

    @Column(name = "aggregate_id", nullable = false)
    private UUID aggregateId;

    @Column(name = "occurred_at", nullable = false)
    private Instant occurredAt;

    @Column(name = "payload", nullable = false, columnDefinition = "TEXT")
    private String payload;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CfpOutboxStatus status;

    @Column(name = "attempt_count", nullable = false)
    private int attemptCount;

    @Column(name = "next_attempt_at", nullable = false)
    private Instant nextAttemptAt;

    @Column(name = "published_at")
    private Instant publishedAt;

    @Column(name = "last_error", columnDefinition = "TEXT")
    private String lastError;

    public CfpOutboxEventEntity() {
    }

    public CfpOutboxEventEntity(UUID eventId, String eventType, int eventVersion, UUID aggregateId,
                                Instant occurredAt, String payload, CfpOutboxStatus status,
                                int attemptCount, Instant nextAttemptAt) {
        this.eventId = eventId;
        this.eventType = eventType;
        this.eventVersion = eventVersion;
        this.aggregateId = aggregateId;
        this.occurredAt = occurredAt;
        this.payload = payload;
        this.status = status;
        this.attemptCount = attemptCount;
        this.nextAttemptAt = nextAttemptAt;
    }

    public boolean isPublishableAt(Instant now) {
        return status == CfpOutboxStatus.PENDING && !nextAttemptAt.isAfter(now);
    }

    public void markPublished(Instant publishedAt) {
        this.status = CfpOutboxStatus.PUBLISHED;
        this.publishedAt = publishedAt;
    }

    /**
     * Record a failed publication attempt: increment the attempt counter, store a
     * sanitized error, schedule the next attempt, and give up (FAILED) once the
     * maximum number of attempts is reached.
     */
    public void recordFailure(String error, Instant nextAttemptAt, int maxAttempts) {
        this.attemptCount += 1;
        this.lastError = error;
        this.nextAttemptAt = nextAttemptAt;
        this.status = this.attemptCount >= maxAttempts ? CfpOutboxStatus.FAILED : CfpOutboxStatus.PENDING;
    }

    public UUID getEventId() {
        return eventId;
    }

    public String getEventType() {
        return eventType;
    }

    public int getEventVersion() {
        return eventVersion;
    }

    public UUID getAggregateId() {
        return aggregateId;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }

    public String getPayload() {
        return payload;
    }

    public CfpOutboxStatus getStatus() {
        return status;
    }

    public int getAttemptCount() {
        return attemptCount;
    }

    public Instant getNextAttemptAt() {
        return nextAttemptAt;
    }

    public Instant getPublishedAt() {
        return publishedAt;
    }

    public String getLastError() {
        return lastError;
    }
}
