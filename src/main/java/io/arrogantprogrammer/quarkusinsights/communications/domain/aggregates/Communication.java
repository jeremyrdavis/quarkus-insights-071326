package io.arrogantprogrammer.quarkusinsights.communications.domain.aggregates;

import io.arrogantprogrammer.quarkusinsights.communications.domain.CommunicationType;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * A Communications-owned record that an outbound message was warranted by a
 * source event, plus the deliveries that carry it. The {@code sourceEventId}
 * anchors idempotency: at most one Communication per source event.
 */
public class Communication {

    private UUID id;
    private UUID sourceEventId;
    private CommunicationType type;
    private Instant createdAt;
    private List<CommunicationDelivery> deliveries;

    public Communication() {
    }

    public Communication(UUID id, UUID sourceEventId, CommunicationType type, Instant createdAt,
                         List<CommunicationDelivery> deliveries) {
        this.id = id;
        this.sourceEventId = sourceEventId;
        this.type = type;
        this.createdAt = createdAt;
        this.deliveries = deliveries;
    }

    /**
     * Communications policy for an accepted session proposal: compose one email
     * to the presenter and queue it for delivery. This is the only place the
     * accepted-proposal message text lives.
     */
    public static Communication forAcceptedSessionProposal(
            UUID sourceEventId,
            String presenterFirstName,
            String presenterEmail,
            String proposalTitle,
            Instant createdAt) {
        String subject = "Your session proposal was accepted";
        String body = "Hi " + presenterFirstName + ",\n\n"
                + "Your session proposal \"" + proposalTitle + "\" has been accepted.\n\n"
                + "Thank you for submitting.";
        CommunicationDelivery delivery = CommunicationDelivery.email(presenterEmail, subject, body, createdAt);
        return new Communication(
                UUID.randomUUID(),
                sourceEventId,
                CommunicationType.SESSION_PROPOSAL_ACCEPTED,
                createdAt,
                List.of(delivery));
    }

    public UUID getId() {
        return id;
    }

    public UUID getSourceEventId() {
        return sourceEventId;
    }

    public CommunicationType getType() {
        return type;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public List<CommunicationDelivery> getDeliveries() {
        return deliveries;
    }
}
