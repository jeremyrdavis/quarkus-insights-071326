package io.arrogantprogrammer.quarkusinsights.cfp.domain.events;

import io.arrogantprogrammer.quarkusinsights.shared.domain.PublishedEvent;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Published event: a session proposal was accepted. This is the CFP context's
 * external contract — it carries everything a consumer needs (the presenter
 * snapshot and proposal title) so no consumer must query the CFP subdomain.
 *
 * <p>The email is a plain {@code String}, not the CFP {@code EmailAddress} value
 * object, to keep the published contract free of CFP internal types.
 */
public record SessionProposalAcceptedEvent(
        UUID eventId,
        Instant occurredAt,
        int eventVersion,
        UUID proposalId,
        UUID cfpId,
        String proposalTitle,
        UUID presenterId,
        String presenterFirstName,
        String presenterLastName,
        String presenterEmail
) implements PublishedEvent {

    /** Stable wire identifier — deliberately not derived from the Java class name. */
    public static final String EVENT_TYPE = "cfp.session-proposal.accepted";

    public static final int CURRENT_VERSION = 1;

    public SessionProposalAcceptedEvent {
        Objects.requireNonNull(eventId, "eventId is required");
        Objects.requireNonNull(occurredAt, "occurredAt is required");
        Objects.requireNonNull(proposalId, "proposalId is required");
        Objects.requireNonNull(cfpId, "cfpId is required");
        Objects.requireNonNull(presenterId, "presenterId is required");
        requireNotBlank(proposalTitle, "proposalTitle");
        requireNotBlank(presenterFirstName, "presenterFirstName");
        requireNotBlank(presenterLastName, "presenterLastName");
        requireNotBlank(presenterEmail, "presenterEmail");
        if (eventVersion <= 0) {
            throw new IllegalArgumentException("eventVersion must be positive");
        }
    }

    private static void requireNotBlank(String value, String field) {
        Objects.requireNonNull(value, field + " is required");
        if (value.isBlank()) {
            throw new IllegalArgumentException(field + " must not be blank");
        }
    }
}
