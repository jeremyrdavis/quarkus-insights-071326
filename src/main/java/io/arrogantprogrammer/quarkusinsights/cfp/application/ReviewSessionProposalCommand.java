package io.arrogantprogrammer.quarkusinsights.cfp.application;

import io.arrogantprogrammer.quarkusinsights.cfp.domain.SessionProposalStatus;

import java.util.Objects;
import java.util.UUID;

public record ReviewSessionProposalCommand(UUID proposalId, SessionProposalStatus newStatus) {
    public ReviewSessionProposalCommand {
        Objects.requireNonNull(proposalId, "proposalId is required");
        Objects.requireNonNull(newStatus, "newStatus is required");
    }
}
