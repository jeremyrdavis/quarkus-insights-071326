package io.arrogantprogrammer.quarkusinsights.cfp.domain.events;

import io.arrogantprogrammer.quarkusinsights.cfp.domain.EmailAddress;
import io.arrogantprogrammer.quarkusinsights.cfp.domain.SessionProposalStatus;
import io.arrogantprogrammer.quarkusinsights.shared.domain.DomainEvent;

import java.util.UUID;

public record SessionProposalStatusChangedEvent(
        UUID proposalId,
        UUID cfpId,
        String proposalTitle,
        EmailAddress presenterEmail,
        SessionProposalStatus previousStatus,
        SessionProposalStatus newStatus
) implements DomainEvent {
}
