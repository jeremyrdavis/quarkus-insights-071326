package io.arrogantprogrammer.quarkusinsights.communications.domain.valueobjects;

import java.util.Objects;
import java.util.UUID;

/**
 * Immutable, Communications-owned description of an email to send. Produced by a
 * {@code CommunicationDelivery} when it is claimed for delivery and consumed by
 * the {@code EmailSender} port. Deliberately free of any mail-framework type so
 * the domain and application layers stay adapter-agnostic.
 */
public record EmailMessage(UUID deliveryId, String to, String subject, String body) {

    public EmailMessage {
        Objects.requireNonNull(deliveryId, "deliveryId is required");
        Objects.requireNonNull(to, "to is required");
        Objects.requireNonNull(subject, "subject is required");
        Objects.requireNonNull(body, "body is required");
    }
}
