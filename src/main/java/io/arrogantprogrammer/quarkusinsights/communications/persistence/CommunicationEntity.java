package io.arrogantprogrammer.quarkusinsights.communications.persistence;

import io.arrogantprogrammer.quarkusinsights.communications.domain.CommunicationType;
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
        name = "communication",
        indexes = @Index(name = "uq_communication_source_event", columnList = "source_event_id", unique = true))
public class CommunicationEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "source_event_id", nullable = false, unique = true, updatable = false)
    private UUID sourceEventId;

    @Enumerated(EnumType.STRING)
    @Column(name = "communication_type", nullable = false)
    private CommunicationType communicationType;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    public CommunicationEntity() {
    }

    public CommunicationEntity(UUID id, UUID sourceEventId, CommunicationType communicationType, Instant createdAt) {
        this.id = id;
        this.sourceEventId = sourceEventId;
        this.communicationType = communicationType;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public UUID getSourceEventId() {
        return sourceEventId;
    }

    public CommunicationType getCommunicationType() {
        return communicationType;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
