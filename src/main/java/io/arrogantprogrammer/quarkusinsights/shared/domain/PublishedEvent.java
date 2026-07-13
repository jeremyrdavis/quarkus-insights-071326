package io.arrogantprogrammer.quarkusinsights.shared.domain;

import java.time.Instant;
import java.util.UUID;

/**
 * Marker for events that form a bounded context's <em>published language</em> —
 * the durable, versioned contract other contexts may observe. Distinct from an
 * internal domain event: a {@code PublishedEvent} carries an identity, an
 * occurrence timestamp, and an explicit contract version so it can be persisted
 * to an outbox and safely deserialized later.
 *
 * <p>No Jakarta, Jackson, JPA, or Quarkus annotations belong on this interface.
 */
public interface PublishedEvent extends DomainEvent {

    UUID eventId();

    Instant occurredAt();

    int eventVersion();
}
