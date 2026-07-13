package io.arrogantprogrammer.quarkusinsights.cfp.application;

import io.arrogantprogrammer.quarkusinsights.cfp.domain.SessionProposalStatus;

import java.util.Objects;
import java.util.UUID;

public record ChangeSessionProposalStatusCommand(UUID proposalId, SessionProposalStatus newStatus) {
    public ChangeSessionProposalStatusCommand {
        Objects.requireNonNull(proposalId, "proposalId is required");
        Objects.requireNonNull(newStatus, "newStatus is required");
    }
}
